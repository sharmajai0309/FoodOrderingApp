package com.Food.dto;

import com.Food.Model.Category;
import com.Food.Model.IngredientItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto {

    private Long id;          // ← REQUIRED: frontend needs this to add item to cart
    private String name;
    private String description;
    private Long price;

    private Category category;
    private List<String> images;
    private boolean isVegetarian;
    private boolean isSeasonal;
    private List<IngredientItem> ingredients;

}
