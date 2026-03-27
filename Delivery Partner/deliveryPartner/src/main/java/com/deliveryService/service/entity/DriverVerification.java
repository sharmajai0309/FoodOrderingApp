package com.deliveryService.service.entity;


import com.deliveryService.service.entity.base.BaseEntity;
import com.deliveryService.service.entity.Enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver_verification")
@Getter
@Setter
public class DriverVerification extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false, unique = true)
    private DeliveryPartner partner;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    // Name of region admin who verified it
    private String verifiedBy;

    private String remarks;

}
