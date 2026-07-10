package com.Food.PubSub;

import com.Food.dto.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Kafka producer service — publishes order lifecycle events.
 *
 * WHY @Async?
 *   Kafka publish is a network call to the broker. If Kafka is slow or
 *   briefly down, we don't want the Stripe /payment/success webhook to
 *   hang. The HTTP response to Stripe must return fast (< 5s) or Stripe
 *   will retry the webhook. @Async puts this on a background thread.
 *
 * WHY try-catch and NOT throw?
 *   If Kafka is down, we must NOT rollback the payment. The order IS paid.
 *   We log the failure and move on. (Future: add DLQ / retry mechanism)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate;

    // Topic name — matches what delivery service listens to
    public static final String ORDER_CONFIRMED_TOPIC = "order.confirmed";

    /**
     * Publish ORDER_CONFIRMED event after payment succeeds.
     *
     * Key = orderId.toString()
     *   → Ensures all events for the same order go to the SAME partition.
     *   → This guarantees order-level message ordering.
     *
     * @param event the confirmed order details
     */
    @Async
    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        try {
            kafkaTemplate.send(
                    ORDER_CONFIRMED_TOPIC,
                    String.valueOf(event.getOrderId()),  // partition key = orderId
                    event
            );
            log.info("[KAFKA] Published ORDER_CONFIRMED → topic={}, orderId={}, restaurantId={}",
                    ORDER_CONFIRMED_TOPIC, event.getOrderId(), event.getRestaurantId());

        } catch (Exception e) {
            // NEVER throw — log and continue. Order is already paid.
            log.error("[KAFKA] Failed to publish ORDER_CONFIRMED for orderId={}: {}",
                    event.getOrderId(), e.getMessage(), e);
        }
    }
}
