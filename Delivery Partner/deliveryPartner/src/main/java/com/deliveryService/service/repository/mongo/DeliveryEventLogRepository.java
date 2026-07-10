package com.deliveryService.service.repository.mongo;

import com.deliveryService.service.entity.mongo.DeliveryEventLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryEventLogRepository extends MongoRepository<DeliveryEventLog, String> {

    List<DeliveryEventLog> findByAssignmentIdOrderByCreatedAtDesc(Long assignmentId);

    List<DeliveryEventLog> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    List<DeliveryEventLog> findByPartnerIdOrderByCreatedAtDesc(Long partnerId);
}
