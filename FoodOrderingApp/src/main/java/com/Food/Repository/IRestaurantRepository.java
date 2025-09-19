package com.Food.Repository;

import com.Food.Model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(value = "SELECT * FROM restaurant r WHERE r.owner_id = :userId", nativeQuery = true)
    List<Restaurant> findByOwnerIdsql(@Param("userId") Long userId);


    // Standard derived query (Preferred for simple cases)
    List<Restaurant> findByOwnerId(Long userId);


    // JPQL query to Search by Restaurant or cusine type
    @Query("SELECT r FROM Restaurant r WHERE " +
                "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                "LOWER(r.cusineType) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
        List<Restaurant> findBySearchQuery(@Param("searchQuery") String searchQuery);

    @Query("SELECT r FROM Restaurant r WHERE r.open = true")
    List<Restaurant> findAllOpenRestaurants();


}