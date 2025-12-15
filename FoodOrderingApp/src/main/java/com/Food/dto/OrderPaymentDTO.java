package com.Food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentDTO {

    private Long orderId;
    private Long amountInRupees;
    private String description;

}
