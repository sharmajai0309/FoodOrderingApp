package com.deliveryService.service.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Event DTO received from Kafka when an order is confirmed (PAID).
 * It mirrors the producer's payload from the FoodOrderingApp.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmedEvent {
    private Long orderId;
    private Long restaurantId;
    private String restaurantName;
    private String customerName;
    private String customerAddress;
    private Long totalAmount;
    private String status;
    private Instant occurredAt;
}
