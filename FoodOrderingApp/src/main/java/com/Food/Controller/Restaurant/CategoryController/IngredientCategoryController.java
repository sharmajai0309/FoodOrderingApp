package com.Food.Controller.Restaurant.CategoryController;


import com.Food.Model.IngredientCategory;
import com.Food.Response.ApiResponse;
import com.Food.Service.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
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
     * 🎯 USE CASE: Add new food category to restaurant
     */
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







}
