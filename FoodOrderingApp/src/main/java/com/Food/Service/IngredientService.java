package com.Food.Service;

import com.Food.Model.IngredientCategory;
import com.Food.Model.IngredientItem;
import com.Food.Response.ApiResponse;

import java.util.List;

public interface IngredientService {

    /**
     * Create a new ingredient category
     *
     * @param categoryName the name of the category
     * @param restaurantId the ID of the restaurant
     * @return the created IngredientCategory
     */
    IngredientCategory createIngredientCategory(String categoryName, Long restaurantId) throws Exception;

    /**
     * Find ingredient category by ID
     *
     * @param categoryId the ID of the category
     * @return the found IngredientCategory
     */
    IngredientCategory getIngredientCategoryById(Long categoryId) throws Exception;

    /**
     * Find all ingredient categories for a restaurant
     *
     * @param restaurantId the ID of the restaurant
     * @return list of ingredient categories
     */
    List<String> getIngredientCategoriesByRestaurantId(Long restaurantId);

    /**
     * Find all ingredient items for a restaurant
     *
     * @param restaurantId the ID of the restaurant
     * @return list of ingredient items
     */
    List<IngredientItem> getIngredientItemsByRestaurantId(Long restaurantId);

    /**
     * Create a new ingredient item
     *
     * @param restaurantId the ID of the restaurant
     * @param ingredientName the name of the ingredient
     * @param categoryId the ID of the category
     * @return the created IngredientItem
     */
    IngredientItem createIngredientItem(Long restaurantId, String ingredientName, Long categoryId) throws Exception;

    /**
     * Update ingredient item stock status
     *
     * @param ingredientItemId the ID of the ingredient item
     * @return the updated IngredientItem
     */
    IngredientItem updateIngredientItemStockStatus(Long ingredientItemId);


    /**
     * Get ingredient item
     *
     * @param ingredientItemId the ID of the ingredient item
     * @return IngredientItem
     */
    IngredientItem getIngredientItemById(Long IngredientItemId);


}
