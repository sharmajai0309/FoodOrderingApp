package com.Food.request;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddCartItemQuantityRequest {


    @NotNull(message = "CartItem Id is required")
    @Positive(message = "CartItem Id must be positive")
    private Long cartItemId;


    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 20, message = "Quantity cannot exceed 20")
    private int quantity;



}
