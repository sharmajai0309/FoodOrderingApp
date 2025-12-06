package com.Food.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class AddCartItemIngredientsRequest {

    @NotNull(message = "CartItem Id is required")
    @Positive(message = "CartItem Id must be positive")
    private Long cartItemId;

    @NotNull(message = "Ingredients list cannot be null")
    private List<String> ingredients = new ArrayList<>();


}
