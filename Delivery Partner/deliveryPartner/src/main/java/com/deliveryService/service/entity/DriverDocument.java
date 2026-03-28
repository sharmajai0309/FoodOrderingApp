package com.deliveryService.service.entity;


import com.deliveryService.service.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "driver_document")
@Getter
@Setter
public class DriverDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id",nullable = false)
    private DeliveryPartner partner;


    private String documentType;

    private String documentUrl;

}
