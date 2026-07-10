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
public class OrderDeliveredEvent {
    private Long orderId;
    private Long driverId;
    private Instant deliveredAt;
}
