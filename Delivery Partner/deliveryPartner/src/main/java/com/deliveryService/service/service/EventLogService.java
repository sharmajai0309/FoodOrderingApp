package com.deliveryService.service.service;

import com.deliveryService.service.entity.Enums.AssignmentStatus;
import com.deliveryService.service.entity.mongo.DeliveryEventLog;
import com.deliveryService.service.entity.mongo.DriverActivityLog;
import com.deliveryService.service.repository.mongo.DeliveryEventLogRepository;
import com.deliveryService.service.repository.mongo.DriverActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Event logging service — writes to MongoDB asynchronously.
 *
 * Called internally by DeliveryAssignmentService and DriverAvailabilityService.
 * Async so it never blocks the main transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventLogService {

    private final DeliveryEventLogRepository eventLogRepository;
    private final DriverActivityLogRepository activityLogRepository;

    /**
     * Log a delivery status transition.
     * E.g. ASSIGNED → ACCEPTED, ACCEPTED → PICKED_UP
     */
    @Async
    public void logDeliveryEvent(
            Long assignmentId,
            Long orderId,
            Long partnerId,
            String partnerName,
            AssignmentStatus fromStatus,
            AssignmentStatus toStatus,
            String triggeredBy) {

        try {
            DeliveryEventLog event = new DeliveryEventLog();
            event.setAssignmentId(assignmentId);
            event.setOrderId(orderId);
            event.setPartnerId(partnerId);
            event.setPartnerName(partnerName);
            event.setFromStatus(fromStatus);
            event.setToStatus(toStatus);
            event.setTriggeredBy(triggeredBy);
            event.setNotes(fromStatus + " → " + toStatus);
            eventLogRepository.save(event);
        } catch (Exception e) {
            // Never let logging failure break the main flow
            log.warn("Failed to write delivery event log: {}", e.getMessage());
        }
    }

    /**
     * Log a driver activity (online, offline, accepted, picked, delivered).
     */
    @Async
    public void logDriverActivity(
            Long partnerId,
            String partnerName,
            String activityType,
            Long assignmentId,
            Long orderId,
            String details) {

        try {
            DriverActivityLog activity = new DriverActivityLog();
            activity.setPartnerId(partnerId);
            activity.setPartnerName(partnerName);
            activity.setActivityType(activityType);
            activity.setAssignmentId(assignmentId);
            activity.setOrderId(orderId);
            activity.setDetails(details);
            activityLogRepository.save(activity);
        } catch (Exception e) {
            log.warn("Failed to write driver activity log: {}", e.getMessage());
        }
    }

    // ── Query Methods ──────────────────────────────────────────────────────

    public List<DeliveryEventLog> getEventsByAssignment(Long assignmentId) {
        return eventLogRepository.findByAssignmentIdOrderByCreatedAtDesc(assignmentId);
    }

    public List<DeliveryEventLog> getEventsByOrder(Long orderId) {
        return eventLogRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    public List<DriverActivityLog> getDriverActivity(Long partnerId) {
        return activityLogRepository.findByPartnerIdOrderByCreatedAtDesc(partnerId);
    }
}
