package com.Food.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


public interface FoodSearchProjection {

    Long getId();
    String getName();
    String getDescription();
    Long getPrice();
    Boolean getVegetarian();
    Boolean getSeasonal();
    String getCategoryName();
    List<String> getImages();
}
