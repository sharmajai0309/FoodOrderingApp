package com.deliveryService.service.entity;
import com.deliveryService.service.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver_availability")
@Getter
@Setter
public class DriverAvailability extends BaseEntity {


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false, unique = true)
    private DeliveryPartner partner;

    private boolean isAvailable;


}
