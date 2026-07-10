package com.deliveryService.service.kafka;

import com.deliveryService.service.service.DeliveryAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCancelledKafkaConsumer {

    private final DeliveryAssignmentService assignmentService;

    @KafkaListener(topics = "order.cancelled", groupId = "delivery-service-group")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("Received ORDER_CANCELLED event from Kafka: orderId={}", event.getOrderId());
        try {
            assignmentService.cancelAssignmentByOrder(event.getOrderId(), event.getReason() != null ? event.getReason() : "Cancelled by user");
        } catch (Exception e) {
            log.error("Failed to cleanly cancel assignment for orderId={}: {}", event.getOrderId(), e.getMessage());
        }
    }
}
