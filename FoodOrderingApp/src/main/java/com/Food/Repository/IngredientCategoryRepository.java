package com.Food.Repository;

import com.Food.Model.IngredientCategory;
import com.Food.Model.IngredientItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {

    /**
     * Find all ingredient categories by restaurant ID
     * JPQL: SELECT ic FROM IngredientCategory ic WHERE ic.restaurant.id = :restaurantId
     * SQL Equivalent: SELECT * FROM ingredient_category WHERE restaurant_id = ?
     */
    @Query("SELECT ic.id, ic.name FROM IngredientCategory ic JOIN ic.restaurant r WHERE r.id = :restaurantId")
    List<String> findCategoryNamesByRestaurantId(@Param("restaurantId") Long restaurantId);


}