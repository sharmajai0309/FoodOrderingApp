package com.deliveryService.service.entity.mongo;

import com.deliveryService.service.entity.base.BaseMongoDocument;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB collection: driver_activity_log
 *
 * Logs driver actions throughout their working session.
 * Used for: compliance, performance review, debugging.
 *
 * Example activities:
 *   WENT_ONLINE, WENT_OFFLINE, ORDER_ACCEPTED, ORDER_PICKED_UP, ORDER_DELIVERED, ORDER_CANCELLED
 */
@Getter
@Setter
@Document(collection = "driver_activity_log")
public class DriverActivityLog extends BaseMongoDocument {

    // Reference to PostgreSQL delivery_partner.id
    @Indexed
    private Long partnerId;
    private String partnerName;

    private String activityType;  // e.g. "WENT_ONLINE", "ORDER_ACCEPTED"

    // Optional: linked assignment (null for online/offline events)
    private Long assignmentId;
    private Long orderId;

    private String details;        // human-readable description
}
