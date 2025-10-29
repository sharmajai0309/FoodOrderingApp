package com.Food.Service.ServiceImpl;

import com.Food.Model.Category;
import com.Food.Model.Food;
import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Repository.IFoodRepository;
import com.Food.Service.IFoodService;
import com.Food.Service.IUserServices;
import com.Food.config.CacheConstants;
import com.Food.request.CreateFoodRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@Service
@RequiredArgsConstructor
public class IFoodServiceImpl implements IFoodService {

    private final IFoodRepository foodRepository;
    private final IUserServices IuserService;


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    @CachePut(cacheNames = CacheConstants.FOODS_CACHE,key ="#result.id" )
    public Food createFood(CreateFoodRequest req, Category category, Restaurant restaurant) {
        User currentUser = getCurrentUser();

        long restaurantOwnerId = restaurant.getOwner().getId();
        long currentUserid = currentUser.getId();
        if (!Objects.equals(restaurantOwnerId, currentUserid)) {
            throw new AccessDeniedException("Access denied! You can only create food for your own restaurants. " +
                    "Restaurant ID: " + restaurant.getId() + " is owned by user ID: " + restaurant.getOwner().getId()
            );
        } else {
            Food food = Food.builder()
                    .name(req.getName())
                    .foodcategory(category)
                    .restaurant(restaurant)
                    .description(req.getDescription())
                    .images(req.getImages())
                    .price(req.getPrice())
                    .ingredients(req.getIngredients())
                    .isSeasonal(req.isSeasonal())
                    .isVegetarian(req.isVegetarian())
                    .createdDate(LocalDateTime.now())
                    .build();
            Food savedfood = foodRepository.save(food);
            //adding food in In-Memory Object State
            restaurant.getFoods().add(savedfood);
            return savedfood;
        }


    }


    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    @CacheEvict(value = CacheConstants.FOODS_CACHE, key = "#foodId")
    public void DeleteFood(Long foodId) throws Exception {

        long currentUserid = getCurrentUser().getId();
        Food food = foodRepository.findByIdWithRestaurantAndOwner(foodId)
                .orElseThrow(Exception::new);

        Restaurant restaurant = food.getRestaurant();
        long resOwnerid = restaurant.getOwner().getId();

        if (!Objects.equals(resOwnerid, currentUserid)) {
            throw new AccessDeniedException("Access denied! You can only Delete food for your own restaurants. " +
                    "Restaurant ID: " + restaurant.getId() + " is owned by user ID: " + restaurant.getOwner().getId()
            );
        }
        foodRepository.deleteById(foodId);
    }


    //Get Restaurant Food By restaurant Id
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Page<Food> getResturantFoods(Long restaurantId, boolean isVeg, boolean isNonVeg,
                                        boolean isSeasonal, String foodCategory,
                                        int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(ASC, "price"));
        Boolean vegFilter = null;
        if (isVeg) {
            vegFilter = true;
        } else if (isNonVeg) {
            vegFilter = false;
        }
        Page<Food> foodsByFilters = foodRepository.findFoodsByFilters(restaurantId, vegFilter, isSeasonal, foodCategory, pageable);
        log.info(foodsByFilters.toString());
        return foodsByFilters;
    }


    //Find Food By FoodId
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    @Cacheable(cacheNames = CacheConstants.FOODS_CACHE, key = "#foodId")
    public Food FindfoodById(Long foodId) {
        Optional<Food> foodbyId = foodRepository.findById(foodId);
        if (foodbyId.isEmpty()) throw new EntityNotFoundException("Food Not found with This id {foodId}");
        return foodbyId.get();
    }


    @Override
    @Transactional(readOnly = true)
    public List<Food> SearchFood(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        SearchEngine searchEngine = new SearchEngine();

        // Split into individual words
        String[] words = keyword.trim().split("\\s+");
        List<String> correctedWords = new ArrayList<>();

        //  individually correct
        for (String word : words) {
            String correctedWord = searchEngine.searchKeyword(word);
            correctedWords.add(correctedWord);
        }

        // Corrected words  join
        String finalSearchQuery = String.join(" ", correctedWords);

        // Log for monitoring
        if (!keyword.equalsIgnoreCase(finalSearchQuery)) {
            log.info("Search corrected: '{}' -> '{}'", keyword, finalSearchQuery);
        }

        return foodRepository.searchFood(finalSearchQuery);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Food> getAllVegFoods(Pageable pageable) {
        return foodRepository.findByIsVegetarianTrue(pageable);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<Food> getAllNonVegFoods(Pageable pageable) {
        return foodRepository.findByIsVegetarianFalse(pageable);
    }


    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.FOODS_CACHE, key = "#foodId")
    public Food updateFoodAvailablitySatus(Long foodId) {
        Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("No Food Found with This id : {foodId}"));
        food.setIsAvailable(!food.getIsAvailable());
        return foodRepository.save(food);
    }
}
