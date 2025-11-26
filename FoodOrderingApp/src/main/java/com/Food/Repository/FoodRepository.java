package com.Food.Repository;

import com.Food.Model.Food;
import com.Food.projections.FoodProjection;
import com.Food.projections.FoodSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {


  Page<Food> findByRestaurantId(Long restaurantId, Pageable pageable);

  @Query("SELECT f.id as id, f.name as name, f.description as description, " +
          "f.price as price, f.isVegetarian as vegetarian, f.isSeasonal as seasonal, " +
          "f.foodcategory.name as categoryName, f.images as images " +
          "FROM Food f " +
          "WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
          "OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
          "OR LOWER(f.foodcategory.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<FoodSearchProjection> searchFood(@Param("keyword") String keyword);


  @Query("SELECT f FROM Food f JOIN FETCH f.restaurant r JOIN FETCH r.owner WHERE f.id = :foodId")
  Optional<Food> findByIdWithRestaurantAndOwner(@Param("foodId") Long foodId);

  // find veg food in restaurant By its restaurant id
  Page<Food> findByRestaurantIdAndIsVegetarianTrue(Long restaurantId, Pageable pageable);



  // find Non-veg food in restaurant By its restaurant id
  Page<Food> findByRestaurantIdAndIsVegetarianFalse(Long restaurantId, Pageable pageable);


  @Query("SELECT f FROM Food f WHERE f.restaurant.id = :restaurantId " +
          "AND (:isVeg IS NULL OR f.isVegetarian = :isVeg) " +
          "AND (:isSeasonal IS NULL OR f.isSeasonal = :isSeasonal) " +
          "AND (:categoryName IS NULL OR f.foodcategory.name = :categoryName)")
  Page<Food> findFoodsByFilters(@Param("restaurantId") Long restaurantId,
                                @Param("isVeg") Boolean isVeg,
                                @Param("isSeasonal") Boolean isSeasonal,
                                @Param("categoryName") String categoryName,
                                Pageable pageable);


  @Query("SELECT f.name as name, f.description as description, f.price as price, " +
          "f.foodcategory as foodcategory, f.images as images, " +
          "f.isVegetarian as isVegetarian, f.isSeasonal as isSeasonal, " +
          "f.ingredients as ingredients, " +
          "f.restaurant.id as restaurantId, f.restaurant.owner as restaurantOwner " +
          "FROM Food f WHERE f.isVegetarian = true")
  Page<FoodProjection> findAllVegFoodsProjected(Pageable pageable);

  // find Non-veg food
  @Query("SELECT f.name as name, f.description as description, f.price as price, " +
          "f.foodcategory as foodcategory, f.images as images, " +
          "f.isVegetarian as isVegetarian, f.isSeasonal as isSeasonal, " +
          "f.ingredients as ingredients, " +
          "f.restaurant.id as restaurantId, f.restaurant.owner as restaurantOwner " +
          "FROM Food f WHERE f.isVegetarian = false")
  Page<FoodProjection> findAllNonVegFoodsProjected(Pageable pageable);

//find Count of Veg page Data
 public Long countByIsVegetarianTrue();






}