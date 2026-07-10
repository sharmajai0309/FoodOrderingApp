package com.deliveryService.service.entity;
import com.deliveryService.service.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver_availability")
@Getter
@Setter
public class DriverAvailability extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false, unique = true)
    @JsonIgnore
    private DeliveryPartner partner;

    private boolean isAvailable;

    // Optional: driver's last known location when they went online
    @Column(nullable = true)
    private Double latitude;

    @Column(nullable = true)
    private Double longitude;
}

