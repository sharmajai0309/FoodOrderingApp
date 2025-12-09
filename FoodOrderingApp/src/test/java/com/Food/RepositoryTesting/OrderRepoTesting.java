package com.Food.RepositoryTesting;

import com.Food.Model.Order;
import com.Food.Model.OrderItem;
import com.Food.Repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class OrderRepoTesting {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional
    void testFindByCustomerId() {
        for (Order order : orderRepository.findByCustomerId(1L)) {
            List<OrderItem> items = order.getItems();
            System.out.println(items);
        }

    }

    @Test
    void findByRestaurantId(){
        for (Order order : orderRepository.findByRestaurantId(1L)) {
            System.out.println(order);
        }

    }
}
