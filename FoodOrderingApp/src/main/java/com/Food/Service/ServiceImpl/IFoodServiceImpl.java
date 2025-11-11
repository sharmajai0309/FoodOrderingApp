package com.Food.Service.ServiceImpl;

import com.Food.Model.*;
import com.Food.Repository.IFoodRepository;
import com.Food.Service.IFoodService;
import com.Food.Service.IResturantService;
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
    private final IResturantService IresturantService;


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConstants.FOODS_CACHE, key = "#result.id")
    public Food createFood(CreateFoodRequest req, Restaurant restaurant) {
        User currentUser = getCurrentUser();
        long restaurantOwnerId = restaurant.getOwner().getId();
        long currentUserid = currentUser.getId();

        // ✅ ADMIN ko allow karo - wo kisi bhi restaurant mein food create kar sakta hai
        if (currentUser.getRole().equals(USER_ROLE.ADMIN)) {
            // ADMIN ke liye direct allow - koi check nahi
            Food food = Food.builder()
                    .name(req.getName())
                    .foodcategory(req.getCategory())
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
            restaurant.getFoods().add(savedfood);
            return savedfood;
        }

        // RESTAURANT_ADMIN ownership check
        if (!Objects.equals(restaurantOwnerId, currentUserid)) {
            throw new AccessDeniedException("Access denied! You can only create food for your own restaurants. " +
                    "Restaurant ID: " + restaurant.getId() + " is owned by user ID: " + restaurant.getOwner().getId()
            );
        }

        // RESTAURANT_ADMIN apne restaurant ke liye food create karega
        Food food = Food.builder()
                .name(req.getName())
                .foodcategory(req.getCategory())
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
        restaurant.getFoods().add(savedfood);
        return savedfood;
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


    //Get Restaurant Food By restaurant id
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
    @Cacheable(cacheNames = CacheConstants.FOODS_CACHE, key = "#foodId")
    public Food findfoodById(Long foodId) {
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
    public Food updateFoodAvailablitySatus(User currentUser,Long foodId) {
        Food food = foodRepository.findByIdWithRestaurantAndOwner(foodId).orElseThrow(() -> new EntityNotFoundException(" Food Not found With this food {foodId}"));
        if (currentUser.getRole().equals(USER_ROLE.ADMIN)) {
            food.setIsAvailable(!food.getIsAvailable());
            return foodRepository.save(food);
        }
        User resOwner = food.getRestaurant().getOwner();
        long resOwnerId = resOwner.getId();
        long currentUserId = currentUser.getId();

        if (Objects.equals(resOwnerId, currentUserId)) {
            food.setIsAvailable(!food.getIsAvailable());
            return foodRepository.save(food);
        }
        throw new AccessDeniedException("Access denied! You can only update food for your own restaurants.");

    }

    @Override
    @Transactional
    public void createBulkFoods(List<CreateFoodRequest> requests) throws Exception {
        List<Food> bulkfood = new ArrayList<>();


        for(CreateFoodRequest request : requests){
            Long restaurantId = request.getRestaurantId();
            Restaurant restaurant = IresturantService.findRestaurantById(restaurantId);


            Food food = new Food();
            food.setName(request.getName());
            food.setFoodcategory(request.getCategory());
            food.setRestaurant(restaurant);
            food.setDescription(request.getDescription());
            food.setImages(request.getImages());
            food.setPrice(request.getPrice());
            food.setIngredients(request.getIngredients());
            food.setIsSeasonal(request.isSeasonal());
            food.setIsVegetarian(request.isVegetarian());
            food.setCreatedDate(LocalDateTime.now());
            food.setIsAvailable(true);

            bulkfood.add(food);

        }
        //saving bulk info in  repository
        foodRepository.saveAll(bulkfood);

    }


}
