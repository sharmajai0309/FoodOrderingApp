package com.deliveryService.service.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

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
