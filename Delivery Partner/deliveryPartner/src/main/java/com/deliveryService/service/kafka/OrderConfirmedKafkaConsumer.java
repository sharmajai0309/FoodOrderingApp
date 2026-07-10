package com.deliveryService.service.kafka;

import com.deliveryService.service.dto.request.AssignOrderRequest;
import com.deliveryService.service.dto.response.DeliveryAssignmentResponseDTO;
import com.deliveryService.service.service.DeliveryAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConfirmedKafkaConsumer {

    private final DeliveryAssignmentService assignmentService;

    @KafkaListener(topics = "order.confirmed", groupId = "delivery-service-group")
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Received ORDER_CONFIRMED event from Kafka: orderId={}, restaurant={}",
                event.getOrderId(), event.getRestaurantName());

        try {
            AssignOrderRequest request = new AssignOrderRequest();
            request.setOrderId(event.getOrderId());
            request.setRestaurantId(event.getRestaurantId());
            request.setRestaurantName(event.getRestaurantName());
            request.setCustomerAddress(event.getCustomerAddress());

            // Attempt to assign the order
            DeliveryAssignmentResponseDTO assignment = assignmentService.assignOrder(request);

            log.info("Order {} successfully assigned to driver: {} (assignmentId={})",
                    event.getOrderId(), assignment.getDriverName(), assignment.getId());

        } catch (Exception e) {
            log.error("Failed to assign delivery for orderId={}: {}",
                    event.getOrderId(), e.getMessage());
            // In a production environment with 3 partitions, we might publish this to a DLQ (Dead Letter Queue)
            // or trigger a retry.
        }
    }
}
