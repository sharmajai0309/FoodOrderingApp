package com.Food.Controller.Restaurant.CategoryController;


import com.Food.Model.IngredientItem;
import com.Food.Response.ApiResponse;
import com.Food.Service.IngredientService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/IngredientList")
public class IngredientItemController {

    private final IngredientService ingredientService;



    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse>getIngredientItemsByRestaurantId(@PathVariable @NotNull Long restaurantId){
        List<IngredientItem> ingredientItems = ingredientService.getIngredientItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(ingredientItems,"ingredientItems List "));
    }


    @PostMapping("createIngredientItem/{restaurantId}/{ingredientName}/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse>createIngredientItem(@PathVariable Long restaurantId,
                                                          @PathVariable String ingredientName,
                                                          @PathVariable Long categoryId) throws Exception {
        IngredientItem ingredientItem = ingredientService.createIngredientItem(restaurantId, ingredientName, categoryId);
        return ResponseEntity.ok(ApiResponse.success(ingredientItem,"Ingredient Created"));
    }

    @PutMapping("/update/{ingredientItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse>UpdateIngredientItemStock(@PathVariable Long ingredientItemId ){
        try {
            IngredientItem ingredientItem = ingredientService.updateIngredientItemStockStatus(ingredientItemId);
           return ResponseEntity.ok(ApiResponse.success(ingredientItem,"Stock updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("IngredientItem/{ingredientItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse>getIngredientItem(@PathVariable Long ingredientItemId){
        IngredientItem ingredientItemById = ingredientService.getIngredientItemById(ingredientItemId);
       return ResponseEntity.ok(ApiResponse.success(ingredientItemById,"ingredientItem Found"));
    }









}
