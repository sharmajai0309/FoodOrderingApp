package com.Food.Controller.Restaurant.FoodController;

import com.Food.Model.Food;
import com.Food.Model.User;
import com.Food.Service.IFoodService;
import com.Food.Service.IUserServices;
import com.Food.dto.FoodDto;
import com.Food.projections.FoodSearchProjection;
import com.Food.request.CreateFoodRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/api/Food")
@RequiredArgsConstructor
public class FoodController {

    private final IFoodService IFoodService;
    private final IUserServices IuserService;
    private final ModelMapper modelMapper;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }




//    ResponseEntity<ApiResponse>CreateFood(@RequestBody CreateFoodRequest FoodRequest) {
//
//    }
    private FoodDto foodresponse(Food food){
        return FoodDto.builder()
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .description(food.getDescription())
                .isVegetarian(food.getIsVegetarian())
                .isSeasonal(food.getIsSeasonal())
                .category(food.getFoodcategory())
                .images(food.getImages()).build();

    }

    @GetMapping("/restaurants/{restaurantId}/foods")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<FoodDto>>getRestaurantFoods(
            @PathVariable Long restaurantId,
            @RequestParam(required = false,defaultValue = "true") Boolean isVeg,
            @RequestParam(required = false,defaultValue = "false") Boolean isNonVeg,
            @RequestParam(required = false) Boolean isSeasonal,
            @RequestParam(required = false) String foodCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        Page<Food> restaurantFoods = IFoodService.getResturantFoods(restaurantId, isVeg, isNonVeg,
                isSeasonal, foodCategory, page, size);
        // Transform Food entities to CreateFoodRequest DTOs

        Page<FoodDto> map = restaurantFoods.map(this::foodresponse);
        //or
//        Page<CreateFoodRequest> map = restaurantFoods.map(food -> foodresponse(food));


        return ResponseEntity.ok(map);

    }




    @GetMapping("/restaurants/{keyword}/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<FoodDto>>searchFood(@PathVariable(required = true) String keyword){


        List<FoodSearchProjection> foods = IFoodService.searchFood(keyword);
        List<FoodDto> foodDtos = foods.stream()
                .map(food -> modelMapper.map(food, FoodDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(foodDtos);

    }



}
