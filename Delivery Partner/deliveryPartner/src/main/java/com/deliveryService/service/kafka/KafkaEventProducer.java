package com.deliveryService.service.kafka;

import com.deliveryService.service.dto.response.DeliveryAssignmentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDriverAssigned(DeliveryAssignmentResponseDTO assignment) {
        DriverAssignedEvent event = new DriverAssignedEvent(
                assignment.getOrderId(),
                assignment.getDriverId(),
                assignment.getDriverName(),
                assignment.getDriverPhone(),
                Instant.now()
        );
        log.info("Publishing driver.assigned for orderId: {}", assignment.getOrderId());
        kafkaTemplate.send("driver.assigned", String.valueOf(assignment.getOrderId()), event);
    }

    public void publishOrderDelivered(DeliveryAssignmentResponseDTO assignment) {
        OrderDeliveredEvent event = new OrderDeliveredEvent(
                assignment.getOrderId(),
                assignment.getDriverId(),
                Instant.now()
        );
        log.info("Publishing order.delivered for orderId: {}", assignment.getOrderId());
        kafkaTemplate.send("order.delivered", String.valueOf(assignment.getOrderId()), event);
    }
}
