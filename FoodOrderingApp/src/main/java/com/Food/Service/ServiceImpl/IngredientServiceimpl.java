package com.Food.Service.ServiceImpl;


import com.Food.Model.Category;
import com.Food.Model.IngredientCategory;
import com.Food.Model.IngredientItem;
import com.Food.Model.Restaurant;
import com.Food.Repository.ICategoryRepository;
import com.Food.Repository.IngredientCategoryRepository;
import com.Food.Repository.IngredientItemRepository;
import com.Food.Response.ApiResponse;
import com.Food.Service.IResturantService;
import com.Food.Service.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientServiceimpl implements IngredientService {

    private final IngredientItemRepository ingredientItemRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final IResturantService IresturantService;
    

    @Override
    @Transactional
    public IngredientCategory createIngredientCategory(String categoryName, Long restaurantId) throws Exception {
        Restaurant restaurantById = IresturantService.findRestaurantById(restaurantId);
        IngredientCategory ingredientCategory = new IngredientCategory();
        ingredientCategory.setName(categoryName);
        ingredientCategory.setRestaurant(restaurantById);
        return ingredientCategoryRepository.save(ingredientCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse getIngredientCategoryById(Long categoryId) {
        try {
            log.debug("Getting ingredient items for category: {}", categoryId);
            if(categoryId == null) return  ApiResponse.error("Category ID cannot be null");

            List<IngredientItem> items = ingredientItemRepository.findByCategoryId(categoryId);

            log.debug("Found {} items for category {}", items.size(), categoryId);

            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage(items.isEmpty() ? "No items Found" : "Items retrieved successfully");
            response.setData(items);
            return response;

        }

        catch (DataAccessException e) {
            log.error("Database error while fetching items for category: {}", categoryId, e);
            return ApiResponse.error("Failed to retrieve items");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getIngredientCategoriesByRestaurantId(Long restaurantId) {
        return  ingredientCategoryRepository.findCategoryNamesByRestaurantId(restaurantId);
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<IngredientItem> getIngredientItemsByRestaurantId(Long restaurantId) {
        return ingredientItemRepository.findIngredientCategoryByRestaurantId(restaurantId);
    }


    @Override
    @Transactional
    public IngredientItem createIngredientItem(Long restaurantId, String ingredientName, Long categoryId) throws Exception {
        log.debug("Creating ingredient item: {} for restaurant: {}, category: {}",
                ingredientName, restaurantId, categoryId);

        if (ingredientName == null) throw new IllegalArgumentException("Ingredient name cannot be empty");

        IngredientCategory ingredientCategory = ingredientCategoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category Not found with this id"));

        Restaurant restaurantById = IresturantService.findRestaurantById(restaurantId);
        if(restaurantId == null) throw new EntityNotFoundException("Restaurant Not found with this id");

        IngredientItem ingredientItem = new IngredientItem();
        ingredientItem.setName(ingredientName);
        ingredientItem.setCategory(ingredientCategory);
        ingredientItem.setInStock(true);
        ingredientItem.setRestaurant(restaurantById);
        ingredientItemRepository.save(ingredientItem);
        return ingredientItem;


    }


    @Override
    @Transactional
    public IngredientItem updateIngredientItemStockStatus(Long ingredientItemId) {
        log.debug("Toggling stock status for ingredient item: {}", ingredientItemId);

        IngredientItem item = ingredientItemRepository.findById(ingredientItemId)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient item not found with id: " + ingredientItemId));

        boolean newStatus = !item.isInStock();
        item.setInStock(newStatus);

        log.info("Stock status toggled for ingredient {}: {} -> {}",
                ingredientItemId, !newStatus, newStatus);

        return item;
    }
}
