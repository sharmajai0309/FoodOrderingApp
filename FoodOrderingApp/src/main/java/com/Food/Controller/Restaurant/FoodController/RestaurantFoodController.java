package com.Food.Controller.Restaurant.FoodController;

import com.Food.Model.Food;
import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Response.ApiResponse;
import com.Food.Service.IFoodService;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.request.CreateFoodRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/Restaurant/Food")
@RequiredArgsConstructor
@Slf4j
public class RestaurantFoodController {

    private final IFoodService IfoodService;
    private final IUserServices IuserService;
    private final IResturantService IresturantService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }

    // Only RestaurantAdmin and Admin Can Create food
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse<Food>> createFood(@RequestBody CreateFoodRequest request) throws Exception {
        Restaurant restaurant = IresturantService.findRestaurantById(request.getRestaurantId());
        Food food = IfoodService.createFood(request,restaurant);

        return ResponseEntity.ok(ApiResponse.success(food, "Food '" + food.getName() + "' created successfully"));
    }

    //Method for bulk food Entries

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createBulkFood(@RequestBody List<CreateFoodRequest> requests) throws Exception {
        IfoodService.createBulkFoods(requests);
        return ResponseEntity.ok(ApiResponse.success("Bulk Food Saved"));
    }


    @DeleteMapping("/{foodId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFood(@PathVariable Long foodId) throws Exception {
        IfoodService.DeleteFood(foodId);
        return ResponseEntity.ok(ApiResponse.success("Food Deleted with Id: " + foodId));
    }

    @GetMapping("/{foodId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse<Food>> FindFoodById(@PathVariable Long foodId){
        Food food = IfoodService.findfoodById(foodId);
        return ResponseEntity.ok(ApiResponse.success(food,"Food Retrieved with id: " + foodId));
    }

//    @GetMapping("/VegFood")
//    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
//    ResponseEntity<FoodProjection> getAllVegFood(@RequestParam(defaultValue = "0") int page,
//                                                 @RequestParam(defaultValue = "10") int size){
//        Pageable pageable = PageRequest.of(page,size, Sort.Direction.ASC);
//        Page<Food> allVegFoods = IfoodService.getAllVegFoods(pageable);
//        return ResponseEntity.ok(ApiResponse.success(allVegFoods,"All veg Food"));
//    }
//    @GetMapping("/NonVegFood")
//    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
//    ResponseEntity<ApiResponse> getAllNonVegFood(@RequestParam(defaultValue = "0") int page,
//                                              @RequestParam(defaultValue = "10") int size){
//        Pageable pageable = PageRequest.of(page,size, Sort.Direction.ASC);
//        Page<Food> allVegFoods = IfoodService.getAllNonVegFoods(pageable);
//        return ResponseEntity.ok(ApiResponse.success(allVegFoods,"All Non-veg Food"));
//    }

    @PutMapping("/{foodid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> updateFoodAvailablityStatus(@PathVariable Long foodid){
        User currentUser = getCurrentUser();
        Food food = IfoodService.updateFoodAvailablitySatus(currentUser, foodid);        return ResponseEntity.ok(ApiResponse.success(food.getIsAvailable(),"Food Availability Status Updated"));
    }
}
