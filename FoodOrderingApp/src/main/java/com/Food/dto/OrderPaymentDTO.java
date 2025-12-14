package com.Food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderPaymentDTO {

    private Long orderId;
    private Long amountInRupees;
    private String description;

}
