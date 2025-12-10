package com.Food.Repository;

import com.Food.Model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    // Finding order by Customer I'd
    public List<Order> findByCustomerId(Long customerId);

    // Finding order by restaurant I'd
    public List<Order> findByRestaurantId(Long restaurantId);


//    Optimised query for findBy Id
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findOrderByIdOnly(@Param("id") Long id);


    // For updating order status ONLY - minimal fetch
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findOrderByIdForUpdate(@Param("id") Long id);


    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.customer c " +
            "LEFT JOIN FETCH o.deliveryAddress a " +
            "LEFT JOIN FETCH o.restaurant r " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.food f " +
            "LEFT JOIN FETCH f.foodcategory " +
            "WHERE c.id = :customerId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findOrdersWithEverything(@Param("customerId") Long customerId);


    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.customer c " +
            "LEFT JOIN FETCH o.deliveryAddress a " +
            "LEFT JOIN FETCH o.restaurant r " +
            "WHERE o.restaurant.id = :restaurantId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findOrdersByRestaurantIdWithEverything(@Param("restaurantId") Long restaurantId);







}