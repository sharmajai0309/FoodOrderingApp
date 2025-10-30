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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/admin/Food")
@RequiredArgsConstructor
public class AdminFoodController {

    private final IFoodService IFoodService;
    private final IUserServices IuserService;
    private final IResturantService IresturantService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }












}
