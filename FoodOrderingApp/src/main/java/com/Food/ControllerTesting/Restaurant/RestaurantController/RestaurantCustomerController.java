package com.Food.ControllerTesting.Restaurant.RestaurantController;

import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Response.ApiResponse;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.dto.RestaurantDto;
import com.Food.exceptions.CustomException.RestaurantNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/restaurants")
@AllArgsConstructor
@Slf4j
public class RestaurantCustomerController {

    private IResturantService IresturantService;

    private final IUserServices IuserService;
    // Extract Name from incoming token
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }



    @GetMapping("/search/{SearchKeyword}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<Restaurant>>>SearchRestaurant(@PathVariable String SearchKeyword) throws RestaurantNotFoundException {
        List<Restaurant> restaurants = IresturantService.searchRestaurant(SearchKeyword);
        ApiResponse<List<Restaurant>> resposnse;
        if(!restaurants.isEmpty()){
            resposnse = ApiResponse.success(restaurants,"Available Restaurant");
        }
        else{
            resposnse = ApiResponse.success(Collections.emptyList(),"No Available Restaurant");
        }
        return ResponseEntity.ok(resposnse);
    }


//    @GetMapping("/getallrestaurant")
//    public ResponseEntity<ApiResponse<List<Restaurant>>>getAllRestaurant(){
//        List<Restaurant> allRestaurants = resturantService.findAllRestaurants();
//        return ResponseEntity.ok(ApiResponse.success(allRestaurants,"List Of All Restaurant"));
//    }


    @GetMapping("/allRestaurants")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>>allRestaurants(@RequestParam int pageNo, @RequestParam int pageSize){
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        log.info(String.valueOf(pageNo));
        log.info(String.valueOf(pageSize));
        Page<Restaurant> allRestaurants = IresturantService.findAllRestaurants(pageRequest);
        Map<String, Object> response = Map.of(
                "Restaurants",allRestaurants.getContent(),
                 "CurrentPage",pageNo,
        "TotalItems",allRestaurants.getTotalElements(),
        "TotalPages",allRestaurants.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse>getRestaurantById(@PathVariable long restaurantId) throws Exception {
        log.info("In Controller Level find By Restaurant");
        User currentUser = getCurrentUser();
        Restaurant restaurantById = IresturantService.findRestaurantById(restaurantId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(restaurantById, "Restaurant retrieved successfully"));
    }


    @PutMapping("/{restaurantId}/favorite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RestaurantDto>> addToFavorites(@PathVariable Long restaurantId) throws Exception {
        log.info("In Controller Level addToFavorites");

        User currentUser = getCurrentUser();
        log.info("Current user: {}", currentUser.getUsername());
        RestaurantDto restaurantDto = IresturantService.addToFavourite(restaurantId, currentUser);

        ApiResponse<RestaurantDto> response = ApiResponse.success(
                restaurantDto,
                "Restaurant added to favorites successfully"
        );

        return ResponseEntity.ok(response);
    }



}







