package com.Food.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Kafka event published to "order.confirmed" topic when a payment succeeds.
 *
 * Producer: FoodOrderingApp (restaurant service)
 * Consumer: DeliveryPartnerService
 *
 * Why a separate DTO and not the Order entity?
 * → Order entity has JPA lazy proxies that can't be serialized to JSON.
 * → This POJO contains only what the delivery service needs.
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

    // Full address string: "42 MG Road, Bangalore, 560001"
    private String customerAddress;

    private Long totalAmount;

    // Always "PAID" when sent from PaymentController
    private String status;

    // When the payment was confirmed
    private Instant occurredAt;
}
