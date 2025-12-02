package com.Food.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
public class AddcartItemRequest {

    private Long foodId;
    private int quantity;
    private List<String>ingredients;


}
