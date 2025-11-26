package com.Food.Repository;

import com.Food.Model.Category;
import com.Food.Model.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
/**
 * Service interface for managing food categories.
 * Provides operations for creating, retrieving, updating, and deleting categories.
 *
 * @author Your Name
 * @version 1.0
 * @since 2024
 */
public interface ICategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Creates a new category for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant to associate with the category
     * @return the newly created Category entity
     * @throws IllegalArgumentException if name is null/empty or restaurantId is invalid
     * @throws AccessDeniedException if user doesn't have permission to access the restaurant
     * @throws EntityNotFoundException if restaurant with given ID doesn't exist
     *
     * @apiNote Only ADMIN or RESTAURANT_ADMIN roles can create categories
     * @see Category
     * @see Restaurant
     */

    @Query("SELECT c.name FROM Category c WHERE c.restaurant.id = :restaurantId")
    public List<String>findCategoryNamesByResturantId(@Param("restaurantId")Long restaurantId);




}