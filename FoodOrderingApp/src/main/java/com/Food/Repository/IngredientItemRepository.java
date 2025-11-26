package com.Food.Repository;

import com.Food.Model.IngredientCategory;
import com.Food.Model.IngredientItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientItemRepository extends JpaRepository<IngredientItem, Long> {

    /**
     * Find all ingredient items by category ID with JOIN FETCH queries
     * Optimized version for better performance
     */
    @EntityGraph(attributePaths = {"category"})
    List<IngredientItem> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT ii FROM IngredientItem ii " +
            "JOIN FETCH ii.category " +
            "JOIN FETCH ii.restaurant " +
            "WHERE ii.restaurant.id = :restaurantId")
    List<IngredientItem> findIngredientCategoryByRestaurantId(@Param("restaurantId") Long restaurantId);


//    List<IngredientItem> findByRestaurantId(Long restaurantId);










}