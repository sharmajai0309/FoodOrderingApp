package com.Food.Repository;

import com.Food.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    // Finding order by Customer I'd
    public List<Order> findByCustomerId(Long customerId);

    // Finding order by restaurant I'd
    public List<Order> findByRestaurantId(Long restaurantId);

}