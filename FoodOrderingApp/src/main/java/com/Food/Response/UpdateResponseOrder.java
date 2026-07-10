package com.Food.Response;

import com.Food.Model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateResponseOrder {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Order status cannot be null")
    private OrderStatus orderStatus;

    private String notes;
}
