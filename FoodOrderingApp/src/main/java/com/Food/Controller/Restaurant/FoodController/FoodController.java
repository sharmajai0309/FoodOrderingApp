package com.Food.Controller.Restaurant.FoodController;

import com.Food.Model.Food;
import com.Food.Model.User;
import com.Food.Service.IFoodService;
import com.Food.Service.IUserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/Food")
@RequiredArgsConstructor
public class FoodController {

    private final IFoodService IFoodService;
    private final IUserServices IuserService;
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }




//    ResponseEntity<ApiResponse>CreateFood(@RequestBody CreateFoodRequest FoodRequest) {
//
//    }

    @GetMapping("/restaurants/{restaurantId}/foods")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<Food>>getRestaurantFoods(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Boolean isVeg,
            @RequestParam(required = false) Boolean isNonVeg,
            @RequestParam(required = false) Boolean isSeasonal,
            @RequestParam(required = false) String foodCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Food> restaurantFoods = IFoodService.getResturantFoods(restaurantId, isVeg, isNonVeg,
                isSeasonal, foodCategory, page, size);
        return ResponseEntity.ok(restaurantFoods);

    }



}
