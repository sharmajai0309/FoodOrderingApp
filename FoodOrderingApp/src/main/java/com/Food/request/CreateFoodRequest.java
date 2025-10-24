package com.Food.request;


import com.Food.Model.Category;
import com.Food.Model.IngredientItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateFoodRequest {

    private String name ;
    private String description;
    private Long price;
    private Category category;
    private List<String> images;
    private Long restaurantId;
    private boolean isVegetarian;
    private boolean isSeasonal;
    private List<IngredientItem> ingredients;



    


}
