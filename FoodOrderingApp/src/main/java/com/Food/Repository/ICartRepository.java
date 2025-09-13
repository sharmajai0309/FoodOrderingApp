package com.Food.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Food.Model.Cart;

@Repository
public interface ICartRepository extends JpaRepository<Cart,Long>{

}
