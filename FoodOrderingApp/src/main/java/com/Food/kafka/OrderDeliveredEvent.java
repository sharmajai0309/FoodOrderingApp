package com.Food.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Inbound Kafka event from delivery service — topic: "order.delivered"
 * Published when a driver marks an order as delivered.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveredEvent {
    private Long orderId;
    private Long driverId;
    private Instant deliveredAt;
}
