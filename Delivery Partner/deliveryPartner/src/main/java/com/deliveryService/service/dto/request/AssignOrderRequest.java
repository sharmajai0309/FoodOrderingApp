package com.deliveryService.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Called by the main food ordering service when an order needs a driver.
 */
@Getter
@Setter
public class AssignOrderRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    private String restaurantName;

    @NotBlank(message = "Customer address is required")
    private String customerAddress;
}
