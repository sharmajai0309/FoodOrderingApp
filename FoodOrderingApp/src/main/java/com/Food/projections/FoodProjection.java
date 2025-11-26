package com.Food.projections;

import com.Food.Model.Category;
import com.Food.Model.IngredientItem;
import com.Food.Model.User;

import java.util.List;

public interface FoodProjection {

    String getName();
    String getDescription();
    Long getPrice();
    Category getFoodcategory();
    List<String> getImages();
    Boolean getIsVegetarian();
    Boolean getIsSeasonal();
    List<IngredientItem> getIngredients();
    RestaurantInfo getRestaurant();

    interface RestaurantInfo {
        Long getId();
        User getOwner();
    }
}
