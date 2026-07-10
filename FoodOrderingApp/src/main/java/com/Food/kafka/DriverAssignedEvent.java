package com.Food.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Inbound Kafka event from delivery service — topic: "driver.assigned"
 * Published when a driver accepts an order.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverAssignedEvent {
    private Long orderId;
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private Instant assignedAt;
}
