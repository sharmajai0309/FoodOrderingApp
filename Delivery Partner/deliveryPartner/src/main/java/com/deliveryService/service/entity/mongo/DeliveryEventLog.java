package com.deliveryService.service.entity.mongo;

import com.deliveryService.service.entity.base.BaseMongoDocument;
import com.deliveryService.service.entity.Enums.AssignmentStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB collection: delivery_event_log
 *
 * Logs every status change in a delivery assignment lifecycle.
 * Used for: audit trail, analytics, debugging, customer tracking.
 *
 * Example events:
 *   ASSIGNED → ACCEPTED → PICKED_UP → DELIVERED
 */
@Getter
@Setter
@Document(collection = "delivery_event_log")
public class DeliveryEventLog extends BaseMongoDocument {

    // Reference to PostgreSQL delivery_assignment.id
    @Indexed
    private Long assignmentId;

    // Reference to PostgreSQL delivery_partner.id
    @Indexed
    private Long partnerId;
    private String partnerName;

    // Reference to main food service order
    @Indexed
    private Long orderId;

    private AssignmentStatus fromStatus;   // previous status
    private AssignmentStatus toStatus;     // new status

    private String triggeredBy;            // "DRIVER" / "SYSTEM" / "ADMIN"
    private String notes;                  // optional extra info
}
