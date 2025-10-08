package com.Food.Controller.Restaurant;


import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Repository.IRestaurantRepository;
import com.Food.Response.ApiResponse;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.dto.RestaurantDto;
import com.Food.exceptions.CustomException.RestaurantNotFoundException;
import com.Food.exceptions.CustomException.UnauthorizedAccessException;
import com.Food.request.CreateRestaurantRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/restaurants")
public class RestaurantAdminController {

    private final IResturantService IresturantService;

    private final IUserServices IuserService;
    private final IRestaurantRepository iRestaurantRepository;

    //Constructor Injection
    public RestaurantAdminController(IResturantService IresturantService, IUserServices IuserService,
                                     IRestaurantRepository iRestaurantRepository){
        this.IresturantService = IresturantService;
        this.IuserService = IuserService;
        this.iRestaurantRepository = iRestaurantRepository;
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
    @DeleteMapping("/{id}")
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
    //Experimental
    @GetMapping("/restaurant")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<List<RestaurantDto>> getAllRestaurantByUserId() {
        try {
            Long currentUserId = getCurrentUser().getId();
            if (currentUserId == null) {
                throw new RestaurantNotFoundException(" User RESTAURANTs not found");
            }
            List<RestaurantDto> restaurantsdto = IresturantService.getRestaurantByUserId(currentUserId);
            return ResponseEntity.ok(restaurantsdto);
        } catch (Exception e) {
            // ✅ Goes to -> GlobalExceptionHandler
            throw e;
        }
    }

    @PutMapping("/{restaurantId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    ResponseEntity<ApiResponse> UpdateRestaurantStatus(@PathVariable Long restaurantId) throws Exception {

        try {
            User currentUser = getCurrentUser();
//        service handles authorization and business logic and status update logic also
            Restaurant restaurant = IresturantService.updateRestaurantStatus(restaurantId, currentUser);

            Restaurant updatedresturantstatus = IresturantService.findRestaurantById(restaurant.getId());
            String status = updatedresturantstatus.isOpen() ? "open" : "false";
            return ResponseEntity.ok(ApiResponse.success("Restaurant status updated with " + status));
        } catch (RestaurantNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Restaurant not found: " + e.getMessage()));

        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied: " + e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating status: " + e.getMessage()));
        }



    }



//Find a Restaurant BY Its ID
    @GetMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse>getRestaurantById(@PathVariable long restaurantId) throws Exception {
        log.info("In Controller Level find By Restaurant");
        User currentUser = getCurrentUser();
        Restaurant restaurantById = IresturantService.findRestaurantById(restaurantId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(restaurantById, "Restaurant retrieved successfully"));
    }













}
