package com.Food.Service.ServiceImpl;

import com.Food.Model.Category;
import com.Food.Model.Food;
import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Repository.IFoodRepository;
import com.Food.Service.IFoodService;
import com.Food.Service.IUserServices;
import com.Food.request.CreateFoodRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDateTime;
import java.util.Objects;

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
    public Food createFood(CreateFoodRequest req, Category category, Restaurant restaurant) {
        User currentUser = getCurrentUser();

        long restaurantOwnerId = restaurant.getOwner().getId();
        long currentUserid = currentUser.getId();
        if(!Objects.equals(restaurantOwnerId,currentUserid)){
            throw new AccessDeniedException("Access denied! You can only create food for your own restaurants. " +
                    "Restaurant ID: " + restaurant.getId() + " is owned by user ID: " + restaurant.getOwner().getId()
            );
        }
        else {
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
    public void DeleteFood(Long foodId) throws Exception {

        long currentUserid = getCurrentUser().getId();

        Food food = foodRepository.findByIdWithRestaurantAndOwner(foodId)
                .orElseThrow(Exception::new);

        Restaurant restaurant = food.getRestaurant();
        long resOwnerid = restaurant.getOwner().getId();

        if(!Objects.equals(resOwnerid,currentUserid)){
            throw new AccessDeniedException("Access denied! You can only Delete food for your own restaurants. " +
                    "Restaurant ID: " + restaurant.getId() + " is owned by user ID: " + restaurant.getOwner().getId()
            );
        }
        foodRepository.deleteById(foodId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Page<Food> getResturantFoods(Long restaurantId, boolean isVeg, boolean isNonVeg, boolean isSeasonal, String foodCategory, int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(ASC,"price"));
        Boolean vegFilter = null;

        if (isVeg) {
            vegFilter = true;
        }
        else if(isNonVeg){
             vegFilter = false;
        }

        Page<Food> foodsByFilters = foodRepository.findFoodsByFilters(restaurantId, vegFilter, isSeasonal, foodCategory, pageable);

        log.info(foodsByFilters.toString());
        return foodsByFilters;



    }

    @Override
    public Food FindById(Long FoodId) {
        return null;
    }

    @Override
    public Page<Food> SearchFood(String Keyword) {
        return null;
    }

    @Override
    public Page<Food> getAllVegFoods(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Food> getAllNonVegFoods(Pageable pageable) {
        return null;
    }

    @Override
    public Food updateFoodAvailablitySatus(Long FoodId) {
        return null;
    }
}
