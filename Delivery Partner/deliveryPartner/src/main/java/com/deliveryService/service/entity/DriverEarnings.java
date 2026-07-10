package com.deliveryService.service.entity;

import com.deliveryService.service.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Tracks earnings per completed delivery.
 *
 * Relations:
 *   driver_earnings.partner_id    → delivery_partner.id
 *   driver_earnings.assignment_id → delivery_assignment.id  (unique — one earning per delivery)
 */
@Entity
@Table(name = "driver_earnings")
@Getter
@Setter
public class DriverEarnings extends BaseEntity {

    // FK → delivery_partner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private DeliveryPartner partner;

    // FK → delivery_assignment (one earning per delivery)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false, unique = true)
    private DeliveryAssignment assignment;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "earned_at", nullable = false)
    private Instant earnedAt;
}
