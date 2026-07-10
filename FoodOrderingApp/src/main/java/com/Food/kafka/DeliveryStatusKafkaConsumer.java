package com.Food.kafka;

import com.Food.Model.Order;
import com.Food.Model.OrderStatus;
import com.Food.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Kafka Consumer — bridges delivery service events back to the food order status.
 *
 * Topics consumed:
 *   - "driver.assigned"  → set order status to OUT_FOR_DELIVERY, stamp deliveryPartnerId
 *   - "order.delivered"  → set order status to DELIVERED, stamp deliveredAt
 *
 * Group: food-service-group (separate from delivery-service-group so both can consume order.confirmed)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryStatusKafkaConsumer {

    private final OrderRepository orderRepository;

    /**
     * Fires when a ZapDash driver accepts & picks up an order.
     * Updates: orderStatus → OUT_FOR_DELIVERY, deliveryPartnerId, outForDeliveryAt
     */
    @KafkaListener(
            topics = "driver.assigned",
            groupId = "food-service-group",
            containerFactory = "deliveryKafkaListenerContainerFactory"
    )
    @Transactional
    public void onDriverAssigned(DriverAssignedEvent event) {
        if (event == null || event.getOrderId() == null) {
            log.warn("[KAFKA] Received null driver.assigned event, skipping.");
            return;
        }
        log.info("[KAFKA] driver.assigned → orderId={}, driverId={}, driver={}",
                event.getOrderId(), event.getDriverId(), event.getDriverName());

        orderRepository.findOrderByIdForUpdate(event.getOrderId()).ifPresentOrElse(order -> {
            // Only advance if it's a valid state to transition from
            if (shouldUpdateOnAssigned(order.getOrderStatus())) {
                order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
                order.setDeliveryPartnerId(event.getDriverId());
                order.setOutForDeliveryAt(LocalDateTime.now());
                orderRepository.save(order);
                log.info("[KAFKA] Order #{} → OUT_FOR_DELIVERY (driver: {})", event.getOrderId(), event.getDriverName());
            } else {
                log.info("[KAFKA] Order #{} already at {}, skipping driver.assigned.", event.getOrderId(), order.getOrderStatus());
            }
        }, () -> log.warn("[KAFKA] Order #{} not found for driver.assigned event.", event.getOrderId()));
    }

    /**
     * Fires when the ZapDash driver taps "Mark Delivered".
     * Updates: orderStatus → DELIVERED, deliveredAt
     */
    @KafkaListener(
            topics = "order.delivered",
            groupId = "food-service-group",
            containerFactory = "deliveryKafkaListenerContainerFactory"
    )
    @Transactional
    public void onOrderDelivered(OrderDeliveredEvent event) {
        if (event == null || event.getOrderId() == null) {
            log.warn("[KAFKA] Received null order.delivered event, skipping.");
            return;
        }
        log.info("[KAFKA] order.delivered → orderId={}, driverId={}", event.getOrderId(), event.getDriverId());

        orderRepository.findOrderByIdForUpdate(event.getOrderId()).ifPresentOrElse(order -> {
            if (order.getOrderStatus() != OrderStatus.DELIVERED
                    && order.getOrderStatus() != OrderStatus.CANCELLED) {
                order.setOrderStatus(OrderStatus.DELIVERED);
                order.setDeliveredAt(
                        event.getDeliveredAt() != null
                                ? LocalDateTime.ofInstant(event.getDeliveredAt(), ZoneOffset.UTC)
                                : LocalDateTime.now()
                );
                orderRepository.save(order);
                log.info("[KAFKA] Order #{} → DELIVERED ✅", event.getOrderId());
            } else {
                log.info("[KAFKA] Order #{} is already {}, skipping order.delivered.", event.getOrderId(), order.getOrderStatus());
            }
        }, () -> log.warn("[KAFKA] Order #{} not found for order.delivered event.", event.getOrderId()));
    }

    private boolean shouldUpdateOnAssigned(OrderStatus current) {
        return current == OrderStatus.PAID
                || current == OrderStatus.ACCEPTED
                || current == OrderStatus.PREPARING
                || current == OrderStatus.PICKED_UP;
    }
}
