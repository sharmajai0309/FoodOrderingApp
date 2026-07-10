package com.deliveryService.service.repository;

import com.deliveryService.service.entity.DeliveryAssignment;
import com.deliveryService.service.entity.Enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {

    Optional<DeliveryAssignment> findByOrderId(Long orderId);

    List<DeliveryAssignment> findByPartner_IdOrderByCreatedAtDesc(Long partnerId);

    List<DeliveryAssignment> findByStatus(AssignmentStatus status);

    boolean existsByOrderId(Long orderId);

    // Check if driver currently has an active (non-completed) assignment
    Optional<DeliveryAssignment> findByPartner_IdAndStatusIn(Long partnerId, List<AssignmentStatus> statuses);
}
