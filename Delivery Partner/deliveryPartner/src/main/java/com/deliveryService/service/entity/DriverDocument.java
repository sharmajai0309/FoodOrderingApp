package com.deliveryService.service.entity;


import com.deliveryService.service.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "driver_document")
@Getter
@Setter
public class DriverDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id",nullable = false)
    @JsonIgnore
    private DeliveryPartner partner;

    private String documentType;

    private String documentUrl;

}
