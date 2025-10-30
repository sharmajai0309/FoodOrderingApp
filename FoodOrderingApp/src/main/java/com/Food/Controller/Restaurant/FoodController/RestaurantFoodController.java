package com.Food.Controller.Restaurant.FoodController;

import com.Food.Model.Category;
import com.Food.Model.Food;
import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Response.ApiResponse;
import com.Food.Service.IFoodService;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.request.CreateFoodRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/Restaurant/Food")
@RequiredArgsConstructor
public class RestaurantFoodController {

    private final IFoodService IfoodService;
    private final IUserServices IuserService;
    private final IResturantService IresturantService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse> createFood(@RequestBody CreateFoodRequest request) throws Exception {
        Restaurant restaurant = IresturantService.findRestaurantById(request.getRestaurantId());
        Food food = IfoodService.createFood(request,restaurant);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Food '" + food.getName() + "' created successfully")
                .data(food)
                .timestamp(String.valueOf(food.getCreatedDate()))
                .build());
    }










}
