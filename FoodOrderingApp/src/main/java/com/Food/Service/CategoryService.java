package com.Food.Service;

import com.Food.Model.Category;
import com.Food.Model.Restaurant;
import com.Food.Response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;


public interface CategoryService {

//    creating Category for a particular restaurant by using restaurant_ID and category name
    public Category createCategory(String name,Long resturantId) throws Exception;

//   find All Category by RestaurantId
    public List<String> findCategoriesByResturantId(Long id);

    //find Category By Category
    public Category findCategoryById(Long id) throws EntityNotFoundException;

}
