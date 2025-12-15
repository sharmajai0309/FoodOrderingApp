package com.Food.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import jakarta.validation.*;

@Data
public class UpdateResponseOrder {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Order status cannot be empty")
    @Pattern(regexp = "PENDING|CONFIRMED|PREPARING|OUT_FOR_DELIVERY|PAYMENT_PENDING|PAID|DELIVERED|CANCELLED|PAYMENT_CANCELLED",
            message = "Invalid order status. Allowed: PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, PAID, PAYMENT_PENDING|PAYMENT_CANCELLED")
    private String orderStatus;

    private String notes;
}
