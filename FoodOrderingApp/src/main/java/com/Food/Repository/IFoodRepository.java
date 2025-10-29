package com.Food.Repository;

import com.Food.Model.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFoodRepository extends JpaRepository<Food, Long> {


  Page<Food> findByRestaurantId(Long restaurantId, Pageable pageable);

  @Query("SELECT f FROM Food f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.foodCategory.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Food>searchFood(@Param("keyword") String keyword);


  @Query("SELECT f FROM Food f JOIN FETCH f.restaurant r JOIN FETCH r.owner WHERE f.id = :foodId")
  Optional<Food> findByIdWithRestaurantAndOwner(@Param("foodId") Long foodId);

  // find veg food in restaurant By its restaurant id
  Page<Food> findByRestaurantIdAndIsVegetarianTrue(Long restaurantId, Pageable pageable);



  // find Non-veg food in restaurant By its restaurant id
  Page<Food> findByRestaurantIdAndIsVegetarianFalse(Long restaurantId, Pageable pageable);


  @Query("SELECT f FROM Food f WHERE f.restaurant.id = :restaurantId " +
          "AND (:isVeg IS NULL OR f.isVegetarian = :isVeg) " +
          "AND (:isSeasonal IS NULL OR f.isSeasonal = :isSeasonal) " +
          "AND (:categoryName IS NULL OR f.foodCategory.name = :categoryName)")
  Page<Food> findFoodsByFilters(@Param("restaurantId") Long restaurantId,
                                @Param("isVeg") Boolean isVeg,
                                @Param("isSeasonal") Boolean isSeasonal,
                                @Param("categoryName") String categoryName,
                                Pageable pageable);

  // find Veg food
  Page<Food> findByIsVegetarianTrue(Pageable pageable);

  // find Non-veg food
  Page<Food> findByIsVegetarianFalse(Pageable pageable);

//find Count of Veg page Data
 public Long countByIsVegetarianTrue();
}