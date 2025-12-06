package com.Food.request;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
public class AddCartItemRequest {

    @NotNull(message = "Food ID is required")
    @Positive(message = "Food ID must be positive")
    private Long foodId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 20, message = "Quantity cannot exceed 20")
    private int quantity;

    @Size(max = 5, message = "Maximum 5 ingredients allowed")
    private List<String>ingredients;


}
