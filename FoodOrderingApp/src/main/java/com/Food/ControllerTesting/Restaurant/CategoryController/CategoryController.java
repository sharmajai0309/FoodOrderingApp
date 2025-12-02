package com.Food.ControllerTesting.Restaurant.CategoryController;


import com.Food.Model.Category;
import com.Food.Model.User;
import com.Food.Response.ApiResponse;
import com.Food.Service.CategoryService;
import com.Food.Service.IUserServices;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;
   private final  IUserServices iuserServices;

   public User getUser(){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       return iuserServices.findByUsername(authentication.getName());
   }




    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse> createCategory(
            @RequestBody  Category category
            ) throws Exception {
        User user = getUser();
        categoryService.createCategory(category.getName(), user.getId());
        return ResponseEntity.ok(ApiResponse.success(category, "Category created successfully"));
    }



    @GetMapping("/find/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
     public ResponseEntity<ApiResponse> getCategorybyId(@PathVariable @Min(1) Long categoryId) throws EntityNotFoundException {
        Category categoryById = categoryService.findCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(categoryById,"Category Found"));
    }

    @GetMapping("/find/categories/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse> getCategoriesbyResturantId(@PathVariable @Min(1) Long restaurantId){
        List<String> categoriesByResturantId = categoryService.findCategoriesByResturantId(restaurantId);
        if(categoriesByResturantId.isEmpty()){
            return ResponseEntity.ok(ApiResponse.error("No Content Found with restaurantId : "+ restaurantId));
        }
        else return ResponseEntity.ok(ApiResponse.success(categoriesByResturantId,"List of Categories"));
    }



}
