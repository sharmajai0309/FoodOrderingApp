package com.Food.Service;

import com.Food.Model.Category;
import com.Food.Model.Food;
import com.Food.Model.Restaurant;
import com.Food.request.CreateFoodRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFoodService {


    public Food createFood(CreateFoodRequest req , Category category , Restaurant restaurant);

    public void DeleteFood(Long foodId) throws Exception;


    public Page<Food> getResturantFoods(Long restaurantId,
                                        boolean isVeg,
                                        boolean isNonVeg,
                                        boolean isSeasonal,
                                        String foodCategory,
                                        int pagenumber,
                                        int pagesize);

    //Only for RestaurantOwner
    public Food FindfoodById(Long FoodId);


    //For Customer
    public List<Food> SearchFood(String Keyword);

    // For veg Foods(Customers)
    public Page<Food> getAllVegFoods(Pageable pageable);


    // for nonVeg Foods(Customers)
    public Page<Food> getAllNonVegFoods(Pageable pageable);


    // for RestaurantOwner
    public Food updateFoodAvailablitySatus(Long FoodId);


    
}
