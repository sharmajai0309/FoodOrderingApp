package com.Food.Service.ServiceImpl;

import com.Food.Model.*;
import com.Food.Repository.FoodRepository;
import com.Food.Service.IFoodService;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.config.CacheConstants;
import com.Food.dto.FoodDto;
import com.Food.projections.FoodProjection;
import com.Food.projections.FoodSearchProjection;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@Service
@RequiredArgsConstructor
public class IFoodServiceImpl implements IFoodService {

    private final FoodRepository foodRepository;
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
    @CacheEvict(value = CacheConstants.FOODS_CACHE, key = "#a0")
    public void DeleteFood(Long foodId) throws EntityNotFoundException {

        log.info("In Service Layer ---> ");
        long currentUserid = getCurrentUser().getId();
        Food food = foodRepository.findByIdWithRestaurantAndOwner(foodId)
                .orElseThrow(EntityNotFoundException::new);
        log.info("True Owner Authorized started");

        Restaurant restaurant = food.getRestaurant();
        long resOwnerid = restaurant.getOwner().getId();

        if (!Objects.equals(resOwnerid, currentUserid)) {
            log.info(" Owner Not  Authorized ");

            throw new AccessDeniedException("Access denied! You can only Delete food for your own restaurants. " +
                    "Restaurant ID: " + restaurant.getId() + " is owned by user ID: " + restaurant.getOwner().getId()

            );

        }
        else{
            log.info("True Authorized completed");
            foodRepository.deleteById(foodId);
            log.info("True Authorized delete completed");
        }



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
    @Cacheable(cacheNames = CacheConstants.FOODS_CACHE, key = "#a0")
    public Food findfoodById(Long foodId) {
        Optional<Food> foodbyId = foodRepository.findById(foodId);
        if (foodbyId.isEmpty()) throw new EntityNotFoundException("Food Not found with This id {foodId}");
        return foodbyId.get();
    }


    @Override
    @Transactional(readOnly = true)
    public List<FoodDto> searchFood(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // =========================
        // 1️⃣ SEARCH KEYWORD CORRECTION
        // =========================
        SearchEngine searchEngine = new SearchEngine();
        String[] words = keyword.trim().split("\\s+");

        List<String> correctedWords = new ArrayList<>();
        for (String word : words) {
            correctedWords.add(searchEngine.searchKeyword(word));
        }

        String finalSearchQuery = String.join(" ", correctedWords);

        if (!keyword.equalsIgnoreCase(finalSearchQuery)) {
            log.info("Search corrected: '{}' -> '{}'", keyword, finalSearchQuery);
        }

        // =========================
        // 2️⃣ FETCH SEARCH PROJECTIONS
        // =========================
        List<FoodSearchProjection> projections =
                foodRepository.searchFood(finalSearchQuery);

        if (projections.isEmpty()) {
            return new ArrayList<>();
        }

        // =========================
        // 3️⃣ EXTRACT FOOD IDS
        // =========================
        List<Long> foodIds = projections.stream()
                .map(FoodSearchProjection::getId)
                .toList();

        // =========================
        // 4️⃣ FETCH IMAGES
        // =========================
        Map<Long, List<String>> imagesMap = new HashMap<>();

        for (Object[] row : foodRepository.findImagesByFoodIds(foodIds)) {
            Long foodId = (Long) row[0];
            String image = (String) row[1];

            imagesMap
                    .computeIfAbsent(foodId, k -> new ArrayList<>())
                    .add(image);
        }

        // =========================
        // 5️⃣ FETCH INGREDIENTS (ENTITY)
        // =========================
        Map<Long, List<IngredientItem>> ingredientMap = new HashMap<>();

        for (Object[] row : foodRepository.findIngredientsByFoodIds(foodIds)) {
            Long foodId = (Long) row[0];
            IngredientItem ingredient = (IngredientItem) row[1]; // ✅ FIXED

            ingredientMap
                    .computeIfAbsent(foodId, k -> new ArrayList<>())
                    .add(ingredient);
        }

        // =========================
        // 6️⃣ BUILD RESPONSE DTOs
        // =========================
        List<FoodDto> response = new ArrayList<>();

        for (FoodSearchProjection food : projections) {

            FoodDto dto = new FoodDto();

            dto.setName(food.getName());
            dto.setDescription(food.getDescription());
            dto.setPrice(food.getPrice());
            dto.setVegetarian(food.getVegetarian());
            dto.setSeasonal(food.getSeasonal());

            // Category (only name from projection)
            Category category = new Category();
            category.setName(food.getCategoryName());
            dto.setCategory(category);

            // Images
            dto.setImages(
                    imagesMap.getOrDefault(food.getId(), new ArrayList<>())
            );

            // Ingredients (ENTITY, no casting)
            dto.setIngredients(
                    ingredientMap.getOrDefault(food.getId(), new ArrayList<>())
            );

            response.add(dto);
        }

        return response;
    }





    //Only For Admin
    @Override
    @Transactional(readOnly = true)
    public Page<FoodProjection> getAllVegFoods(Pageable pageable) {
        Page<FoodProjection> allVegFoodsProjected = foodRepository.findAllVegFoodsProjected(pageable);
        return allVegFoodsProjected;
    }


//only for admin
    @Override
    @Transactional(readOnly = true)
    public Page<FoodProjection> getAllNonVegFoods(Pageable pageable) {
        return foodRepository.findAllNonVegFoodsProjected(pageable);
    }


    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.FOODS_CACHE, key = "#a1")
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
        //saving bulk info in repository
        foodRepository.saveAll(bulkfood);
    }


}
