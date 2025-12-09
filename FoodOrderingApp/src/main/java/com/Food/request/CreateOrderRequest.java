package com.Food.request;


import com.Food.Model.Address;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {


    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Delivery address  is required")
    private Address deliveryAddress;





}
