package com.Food.Controller.Restaurant;


import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Response.ApiResponse;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.dto.ResturantDto;
import com.Food.request.CreateRestaurantRequest;
import jakarta.persistence.EntityNotFoundException;
import org.apache.catalina.mapper.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurants")
public class RestaurantAdminController {

    private final IResturantService IresturantService;

    private final IUserServices IuserService;
//Constructor Injection
    public RestaurantAdminController(IResturantService IresturantService, IUserServices IuserService){
        this.IresturantService = IresturantService;
        this.IuserService = IuserService;
    }

      // Get All Restaurant
     //  FInd Restaurant BY ID
    //   Get Restaurant BY userID

    // Extract Name from incoming token
      private User getCurrentUser() {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          return IuserService.findByUsername(authentication.getName());
      }


      //   Create RESTAURANT
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody CreateRestaurantRequest req){
        String name = getCurrentUser().getUsername();
        User user = IuserService.findByUsername(name);

        Restaurant restaurant = IresturantService.createRestaurant(req,user);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurant);
    }

    //    Update RESTAURANT By Its Owner
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<Restaurant> updateRestaurant(@RequestBody CreateRestaurantRequest UpdateReq, @PathVariable Long id) throws Exception {
        Restaurant restaurant = IresturantService.updateRestaurant(id, UpdateReq);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    //    Delete RESTAURANT By Its Owner
    @DeleteMapping("restaurant/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse>deleteRestaurant(@PathVariable Long id){
        try{
            User currentUser = getCurrentUser();
            IresturantService.deleteRestaurant(currentUser, id);
            return ResponseEntity.ok(ApiResponse.success("Restaurant Deleted Successfully"));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    //get All Restaurant List details by Token(Owner Id)
    @GetMapping("/restaurant")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse> getAllRestaurantByUserId() {
        try {
            Long currentUserId = getCurrentUser().getId();

            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            List<ResturantDto> restaurants = IresturantService.getRestaurantByUserId(currentUserId);

            return ResponseEntity.ok(ApiResponse.success(restaurants, "Restaurants retrieved successfully"));

        } catch (Exception e) {
            // ✅ Goes to -> GlobalExceptionHandler
            throw e;
        }
    }

}
