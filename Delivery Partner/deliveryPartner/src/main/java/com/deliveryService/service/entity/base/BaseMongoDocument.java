package com.deliveryService.service.entity.base;

import java.time.Instant;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class BaseMongoDocument {

    @Id
    private String id;

    private Instant createdAt = Instant.now();

    /*
     * This Base class used in this Class in is used in future implementation of these modules
     * 
     * DeliveryEventLog
     * DriverActivityLog
     * DeliveryTimeline
     * 
     * 
     */
}
