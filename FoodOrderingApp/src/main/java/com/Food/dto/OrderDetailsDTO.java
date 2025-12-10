package com.Food.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {
    private Long id;
    private Long totalAmount;
    private String orderStatus;
    private LocalDateTime createdAt;
    private String deliveryAddressStreet;
    private String deliveryAddressCity;
    private String deliveryAddressZipCode;
    private String deliveryAddressCountry;
    private String customerName;
    private String restaurantName;
    private int totalItem;
    private Long totalPrice;
}
