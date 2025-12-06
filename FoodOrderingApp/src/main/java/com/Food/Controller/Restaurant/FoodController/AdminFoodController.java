package com.Food.Controller.Restaurant.FoodController;

import com.Food.Model.User;
import com.Food.Service.IFoodService;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.projections.FoodProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/admin/Food")
@RequiredArgsConstructor
public class AdminFoodController {

    private final IFoodService IfoodService;
    private final IUserServices IuserService;
    private final IResturantService IresturantService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return IuserService.findByUsername(authentication.getName());
    }


    @GetMapping("/getFood/{type}")
    @PreAuthorize("permitAll()")
    public Page<FoodProjection> getAllFood(@PathVariable String type,
                                           @RequestParam(defaultValue = "0") int pageNo,
                                           @RequestParam(defaultValue = "10") int pageSize) {

        // Validate pagination parameters
        if (pageNo < 0) pageNo = 0;
        if (pageSize <= 0) pageSize = 10;
        if (pageSize > 50) pageSize = 50;

        // FIX: Use pageSize instead of pageNo for second parameter
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("name").ascending());

         return switch (type.toLowerCase()) {
            case "veg" -> IfoodService.getAllVegFoods(pageable);
            case "nonveg" -> IfoodService.getAllNonVegFoods(pageable);
            default -> throw new IllegalArgumentException("Invalid Food type. Only 'veg' or 'nonveg' allowed");
        };
    }












}
