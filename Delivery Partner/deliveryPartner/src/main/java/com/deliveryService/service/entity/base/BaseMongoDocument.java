package com.deliveryService.service.entity.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

/**
 * Base class for all MongoDB documents.
 * Uses Spring Data MongoDB annotations — NOT JPA.
 *
 * Used by: DeliveryEventLog, DriverActivityLog
 */
@Setter
@Getter
public abstract class BaseMongoDocument {

    @Id
    private String id;  // MongoDB ObjectId → stored as String

    @CreatedDate
    private Instant createdAt = Instant.now();
}
