package com.deliveryService.service.entity;

import com.deliveryService.service.entity.base.BaseEntity;
import com.deliveryService.service.entity.Enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a delivery assignment linking an order to a driver.
 * One assignment per order (order_id is unique).
 *
 * Relation: delivery_assignment.partner_id → delivery_partner.id
 */
@Entity
@Table(name = "delivery_assignment")
@Getter
@Setter
public class DeliveryAssignment extends BaseEntity {

    // FK → delivery_partner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private DeliveryPartner partner;

    // From main food service — unique per order
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "customer_address", nullable = false)
    private String customerAddress;

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.valueOf(30.00); // fixed ₹30 default

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "picked_at")
    private Instant pickedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;
}
