package com.deliveryService.service.dto.response;

import com.deliveryService.service.entity.Enums.AssignmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class DeliveryAssignmentResponseDTO {

    private Long id;
    private Long orderId;
    private Long restaurantId;
    private String restaurantName;
    private String customerAddress;
    private BigDecimal deliveryFee;
    private AssignmentStatus status;

    // Driver summary
    private Long driverId;
    private String driverName;
    private String driverPhone;

    private Instant assignedAt;
    private Instant pickedAt;
    private Instant deliveredAt;
}
