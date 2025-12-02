package com.Food.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Food.Model.Cart;

@Repository
public interface ICartRepository extends JpaRepository<Cart,Long> {


    public Cart findByCustomerId(Long userId);


    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.customer WHERE c.customer.id = :userId")
    Cart findByCustomerId2(@Param("userId") Long userId);



}
