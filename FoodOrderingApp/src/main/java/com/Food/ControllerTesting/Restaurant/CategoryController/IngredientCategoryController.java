package com.Food.ControllerTesting.Restaurant.CategoryController;


import com.Food.Model.IngredientCategory;
import com.Food.Response.ApiResponse;
import com.Food.Service.IngredientService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin/IngredientCategory")
public class IngredientCategoryController {

    private final IngredientService ingredientService;

    /**
     *  METHOD: POST
     * INPUT PARAMS:
     * - categoryName (String) - Name of category (e.g., "Vegetables")
     * - restaurantId (Long) - ID of restaurant
     * OUTPUT:
     * - 200: {success:true, data: category, message: "Created"}
     * - 404: {success:false, message: "Error message"}
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    @PostMapping("/create/{categoryName}/{restaurantId}")
    ResponseEntity<ApiResponse>createIngredientCategory(@PathVariable @NotNull String categoryName,
                                                        @PathVariable @NotNull Long restaurantId){
        try{
            IngredientCategory ingredientCategory = ingredientService.createIngredientCategory(categoryName, restaurantId);
            return ResponseEntity.ok(ApiResponse.success(ingredientCategory,"Ingredient category created successfully"));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }

    }

    /**
     *  METHOD: GET
     * INPUT PARAMS:
     * - IngredientCategoryId (String) - Id of category (e.g., "Id")
     * OUTPUT:
     * - 200: {success:true, data: id,name message: "fetched"}
     * - 404: {success:false, message: "Error message"}
     */
    @GetMapping("/{IngredientCategoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse>getIngredientCategoryById(@PathVariable @NotNull Long IngredientCategoryId) throws Exception {
        IngredientCategory ingredientCategoryById = ingredientService.getIngredientCategoryById(IngredientCategoryId);
        log.info("Fetched category: {}", ingredientCategoryById.toString());
        return ResponseEntity.ok(ApiResponse.success(ingredientCategoryById,"IngredientCategory Id fetched"));
    }


    /**
     *  METHOD: GET
     * INPUT PARAMS:
     * - RestaurantId (String) - Id of category (e.g., "Id")
     * OUTPUT:
     * - 200: {success:true, data: id,name message: "fetched"}
     * - 404: {success:false, message: "Error message"}
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    @GetMapping("/Ingredient/{restaurantId}")
    public ResponseEntity<ApiResponse> getIngredientCategoriesByRestaurantId(@PathVariable @NotNull Long restaurantId){
        List<String> ingredientCategories = ingredientService.getIngredientCategoriesByRestaurantId(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(ingredientCategories,"List of Categories "));
    }














}
