package com.Food.Service;

import com.Food.Model.Category;
import com.Food.Model.Food;
import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.projections.FoodProjection;
import com.Food.projections.FoodSearchProjection;
import com.Food.request.CreateFoodRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFoodService {


    public Food createFood(CreateFoodRequest req , Restaurant restaurant);

    public void DeleteFood(Long foodId) throws Exception;


    public Page<Food> getResturantFoods(Long restaurantId,
                                        boolean isVeg,
                                        boolean isNonVeg,
                                        boolean isSeasonal,
                                        String foodCategory,
                                        int pagenumber,
                                        int pagesize);

    //Only for RestaurantOwner
    public Food findfoodById(Long FoodId);

    //For Customer
    public List<FoodSearchProjection> searchFood(String keyword);

    // For veg Foods(Customers)
    public Page<FoodProjection> getAllVegFoods(Pageable pageable);


    // for nonVeg Foods(Customers)
    public Page<FoodProjection> getAllNonVegFoods(Pageable pageable);


    // for RestaurantOwner
    public Food updateFoodAvailablitySatus(User currentUser, Long FoodId);


    //for bulk Services
    void createBulkFoods(List<CreateFoodRequest> requests) throws Exception;
}
