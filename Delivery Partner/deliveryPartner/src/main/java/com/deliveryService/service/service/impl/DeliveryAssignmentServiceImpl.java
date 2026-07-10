package com.deliveryService.service.service.impl;

import com.deliveryService.service.dto.request.AssignOrderRequest;
import com.deliveryService.service.dto.response.DeliveryAssignmentResponseDTO;
import com.deliveryService.service.entity.DeliveryAssignment;
import com.deliveryService.service.entity.DeliveryPartner;
import com.deliveryService.service.entity.DriverEarnings;
import com.deliveryService.service.entity.Enums.AssignmentStatus;
import com.deliveryService.service.exception.customexception.DeliveryAlreadyCompletedException;
import com.deliveryService.service.exception.customexception.InvalidRequestException;
import com.deliveryService.service.exception.customexception.OrderAlreadyPickedException;
import com.deliveryService.service.exception.customexception.OrderAssignmentException;
import com.deliveryService.service.exception.customexception.PartnerNotFoundException;
import com.deliveryService.service.exception.customexception.UnauthorizedPartnerActionException;
import com.deliveryService.service.kafka.KafkaEventProducer;
import com.deliveryService.service.repository.DeliveryAssignmentRepository;
import com.deliveryService.service.repository.DeliveryPartnerRepository;
import com.deliveryService.service.repository.DriverEarningsRepository;
import com.deliveryService.service.service.DeliveryAssignmentService;
import com.deliveryService.service.service.EventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAssignmentServiceImpl implements DeliveryAssignmentService {

    private final DeliveryAssignmentRepository assignmentRepository;
    private final DeliveryPartnerRepository partnerRepository;
    private final DriverEarningsRepository earningsRepository;
    private final EventLogService eventLogService;
    private final KafkaEventProducer kafkaEventProducer;

    private static final BigDecimal DEFAULT_DELIVERY_FEE = BigDecimal.valueOf(30.00);

    @Override
    @Transactional
    public DeliveryAssignmentResponseDTO assignOrder(AssignOrderRequest request) {

        // Guard: already assigned or created?
        if (assignmentRepository.existsByOrderId(request.getOrderId())) {
            throw new OrderAssignmentException("Order " + request.getOrderId() + " is already recorded");
        }

        // Create assignment (PENDING, no driver yet)
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrderId(request.getOrderId());
        assignment.setRestaurantId(request.getRestaurantId());
        assignment.setRestaurantName(request.getRestaurantName());
        assignment.setCustomerAddress(request.getCustomerAddress());
        assignment.setDeliveryFee(DEFAULT_DELIVERY_FEE);
        assignment.setStatus(AssignmentStatus.PENDING);

        DeliveryAssignment saved = assignmentRepository.save(assignment);

        eventLogService.logDeliveryEvent(
                saved.getId(), saved.getOrderId(),
                null, null,
                null, AssignmentStatus.PENDING, "SYSTEM");

        return toDTO(saved);
    }

    @Override
    @Transactional
    public DeliveryAssignmentResponseDTO acceptAssignment(Long assignmentId, Long driverId) {
        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new InvalidRequestException("Assignment not found: " + assignmentId));

        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new OrderAlreadyPickedException(assignment.getOrderId());
        }

        DeliveryPartner driver = partnerRepository.findById(driverId)
                .orElseThrow(() -> new PartnerNotFoundException(driverId));

        assignment.setPartner(driver);
        assignment.setStatus(AssignmentStatus.ACCEPTED);
        assignment.setAssignedAt(Instant.now());
        DeliveryAssignment saved = assignmentRepository.save(assignment);

        eventLogService.logDeliveryEvent(saved.getId(), saved.getOrderId(),
                saved.getPartner().getId(), saved.getPartner().getName(),
                AssignmentStatus.PENDING, AssignmentStatus.ACCEPTED, "DRIVER");
        eventLogService.logDriverActivity(saved.getPartner().getId(), saved.getPartner().getName(),
                "ORDER_ACCEPTED", saved.getId(), saved.getOrderId(), "Driver accepted order");

        // NOTE: We do NOT fire Kafka here. The Food App updates to OUT_FOR_DELIVERY
        // only when the driver physically picks up the food (markPickedUp).
        return toDTO(saved);
    }

    @Override
    @Transactional
    public DeliveryAssignmentResponseDTO markPickedUp(Long assignmentId, Long driverId) {
        DeliveryAssignment assignment = getAndValidateAssignment(assignmentId, driverId, AssignmentStatus.ACCEPTED);
        assignment.setStatus(AssignmentStatus.PICKED_UP);
        assignment.setPickedAt(Instant.now());
        DeliveryAssignment saved = assignmentRepository.save(assignment);
        eventLogService.logDeliveryEvent(saved.getId(), saved.getOrderId(),
                saved.getPartner().getId(), saved.getPartner().getName(),
                AssignmentStatus.ACCEPTED, AssignmentStatus.PICKED_UP, "DRIVER");
        eventLogService.logDriverActivity(saved.getPartner().getId(), saved.getPartner().getName(),
                "ORDER_PICKED_UP", saved.getId(), saved.getOrderId(), "Order picked up from restaurant");

        // NOW fire the Kafka event so Food App advances to OUT_FOR_DELIVERY
        DeliveryAssignmentResponseDTO dto = toDTO(saved);
        kafkaEventProducer.publishDriverAssigned(dto);
        return dto;
    }

    @Override
    @Transactional
    public DeliveryAssignmentResponseDTO markDelivered(Long assignmentId, Long driverId) {
        DeliveryAssignment assignment = getAndValidateAssignment(assignmentId, driverId, AssignmentStatus.PICKED_UP);

        assignment.setStatus(AssignmentStatus.DELIVERED);
        assignment.setDeliveredAt(Instant.now());
        DeliveryAssignment saved = assignmentRepository.save(assignment);

        // Auto-credit earnings
        DriverEarnings earnings = new DriverEarnings();
        earnings.setPartner(saved.getPartner());
        earnings.setAssignment(saved);
        earnings.setAmount(saved.getDeliveryFee());
        earnings.setEarnedAt(Instant.now());
        earningsRepository.save(earnings);

        // Log to MongoDB
        eventLogService.logDeliveryEvent(saved.getId(), saved.getOrderId(),
                saved.getPartner().getId(), saved.getPartner().getName(),
                AssignmentStatus.PICKED_UP, AssignmentStatus.DELIVERED, "DRIVER");
        eventLogService.logDriverActivity(saved.getPartner().getId(), saved.getPartner().getName(),
                "ORDER_DELIVERED", saved.getId(), saved.getOrderId(),
                "Delivered to customer. Earned ₹" + saved.getDeliveryFee());

        DeliveryAssignmentResponseDTO dto = toDTO(saved);
        kafkaEventProducer.publishOrderDelivered(dto);
        return dto;
    }

    @Override
    @Transactional
    public void cancelAssignmentByOrder(Long orderId, String reason) {
        assignmentRepository.findByOrderId(orderId).ifPresent(assignment -> {
            if (assignment.getStatus() != AssignmentStatus.DELIVERED && assignment.getStatus() != AssignmentStatus.CANCELLED) {
                assignment.setStatus(AssignmentStatus.CANCELLED);
                assignment.setCancellationReason(reason);
                assignmentRepository.save(assignment);
                eventLogService.logDriverActivity(
                    assignment.getPartner() != null ? assignment.getPartner().getId() : null,
                    assignment.getPartner() != null ? assignment.getPartner().getName() : "UNASSIGNED",
                    "ORDER_CANCELLED", assignment.getId(), assignment.getOrderId(),
                    "Order cancelled by system: " + reason
                );
            }
        });
    }

    @Override
    @Transactional
    public DeliveryAssignmentResponseDTO cancelAssignment(Long assignmentId, String reason) {
        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new InvalidRequestException("Assignment not found: " + assignmentId));

        if (assignment.getStatus() == AssignmentStatus.DELIVERED) {
            throw new DeliveryAlreadyCompletedException(assignmentId);
        }

        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignment.setCancellationReason(reason);
        return toDTO(assignmentRepository.save(assignment));
    }

    @Override
    public DeliveryAssignmentResponseDTO getByOrderId(Long orderId) {
        return assignmentRepository.findByOrderId(orderId)
                .map(this::toDTO)
                .orElseThrow(() -> new InvalidRequestException("No assignment found for order: " + orderId));
    }

    @Override
    public List<DeliveryAssignmentResponseDTO> getDriverHistory(Long driverId) {
        if (!partnerRepository.existsById(driverId)) {
            throw new PartnerNotFoundException(driverId);
        }
        return assignmentRepository.findByPartner_IdOrderByCreatedAtDesc(driverId)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public List<DeliveryAssignmentResponseDTO> getOpenAssignments() {
        return assignmentRepository.findByStatus(AssignmentStatus.PENDING)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public DeliveryAssignmentResponseDTO getActiveAssignment(Long driverId) {
        if (!partnerRepository.existsById(driverId)) {
            throw new PartnerNotFoundException(driverId);
        }
        return assignmentRepository.findByPartner_IdAndStatusIn(
                driverId,
                java.util.List.of(AssignmentStatus.ACCEPTED, AssignmentStatus.PICKED_UP)
        ).map(this::toDTO).orElse(null);
    }

    // ── Private Helpers ──────────────────────────────────────────────────────

    private DeliveryAssignment getAndValidateAssignment(
            Long assignmentId, Long driverId, AssignmentStatus expectedStatus) {

        DeliveryAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new InvalidRequestException("Assignment not found: " + assignmentId));

        if (assignment.getPartner() == null || !assignment.getPartner().getId().equals(driverId)) {
            throw new UnauthorizedPartnerActionException("This assignment does not belong to driver: " + driverId);
        }

        if (assignment.getStatus() != expectedStatus) {
            throw new OrderAlreadyPickedException(assignment.getOrderId());
        }

        return assignment;
    }

    private DeliveryAssignmentResponseDTO toDTO(DeliveryAssignment a) {
        DeliveryAssignmentResponseDTO dto = new DeliveryAssignmentResponseDTO();
        dto.setId(a.getId());
        dto.setOrderId(a.getOrderId());
        dto.setRestaurantId(a.getRestaurantId());
        dto.setRestaurantName(a.getRestaurantName());
        dto.setCustomerAddress(a.getCustomerAddress());
        dto.setDeliveryFee(a.getDeliveryFee());
        dto.setStatus(a.getStatus());
        dto.setAssignedAt(a.getAssignedAt());
        dto.setPickedAt(a.getPickedAt());
        dto.setDeliveredAt(a.getDeliveredAt());
        
        if (a.getPartner() != null) {
            dto.setDriverId(a.getPartner().getId());
            dto.setDriverName(a.getPartner().getName());
            dto.setDriverPhone(a.getPartner().getPhone());
        }
        return dto;
    }
}
