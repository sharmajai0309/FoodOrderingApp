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
import com.Food.config.CacheConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.Food.config.CacheConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientServiceimpl implements IngredientService {

    private final IngredientItemRepository ingredientItemRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final IResturantService IresturantService;
    

    @Override
    @Transactional
    @CacheEvict(value = INGREDIENT_CATEGORY_RESTAURANT_LIST, key = "#restaurantId")
    public IngredientCategory createIngredientCategory(String categoryName, Long restaurantId) throws Exception {
        Restaurant restaurantById = IresturantService.findRestaurantById(restaurantId);
        IngredientCategory ingredientCategory = new IngredientCategory();
        ingredientCategory.setName(categoryName);
        ingredientCategory.setRestaurant(restaurantById);
        return ingredientCategoryRepository.save(ingredientCategory);
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = INGREDIENT_CATEGORY_SINGLE,key = "#categoryId")
    public IngredientCategory getIngredientCategoryById(Long categoryId) {
       return ingredientCategoryRepository.findByCategoryId(categoryId)
               .orElseThrow(() ->
                       new EntityNotFoundException("Category not found: " + categoryId));
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = INGREDIENT_CATEGORY_RESTAURANT_LIST,key = "#restaurantId")
    public List<String> getIngredientCategoriesByRestaurantId(Long restaurantId) {
        return  ingredientCategoryRepository.findCategoryNamesByRestaurantId(restaurantId);
    }

    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = INGREDIENT_ITEM_RESTAURANT_LIST,key = "#restaurantId")
    public List<IngredientItem> getIngredientItemsByRestaurantId(Long restaurantId) {
        return ingredientItemRepository.findIngredientCategoryByRestaurantId(restaurantId);
    }


    @Override
    @Transactional
    @CacheEvict(value = INGREDIENT_ITEM_RESTAURANT_LIST, key = "#restaurantId")
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
    @Caching(evict = {
            @CacheEvict(value = INGREDIENT_ITEM_SINGLE, key = "#ingredientItemId"),
            @CacheEvict(value = INGREDIENT_ITEM_RESTAURANT_LIST, allEntries = true)
    })
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



    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = INGREDIENT_ITEM_SINGLE,key = "#ingredientItemId")
    public IngredientItem getIngredientItemById(Long ingredientItemId) {
         return ingredientItemRepository.findById(ingredientItemId).orElseThrow(() -> new EntityNotFoundException("IngredientItem Not Found"));
    }
}
