package com.Food.RepositoryTesting;


import com.Food.Model.Cart;
import com.Food.Repository.ICartRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@SpringBootTest
 class CartRepoTesting {

    @Autowired
    private ICartRepository cartRepoTesting;


    @Test
    @Transactional
    public void findcartbyitsid(){
//        Cart cart1 = cartRepoTesting.findByCustomerId2(1L);
//
//        System.out.print(
//                "Total: " + cart1.getTotal() +
//                        ", ID: " + cart1.getId() +
//                        ", Customer: " + cart1.getCustomer() +
//                        ", Items Count: " + cart1.getItems().size()
//        );
    }





}
