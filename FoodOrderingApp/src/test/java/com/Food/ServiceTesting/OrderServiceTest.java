package com.Food.ServiceTesting;

import com.Food.Model.Address;
import com.Food.Model.Order;
import com.Food.Repository.OrderRepository;
import com.Food.Response.ResponseOrder;
import com.Food.Service.OrderService;
import com.Food.request.CreateOrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {

        String actualToken ="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKYWkiLCJpYXQiOjE3NjQ3ODE4NzgsImV4cCI6MTc2NDc5OTg3OH0.cw0yuhAFN6fj7T7nKdvYiYfaImu_51KbUj1nfSmpb18";

        // Parse token and extract username (which is user ID "1")
        // But simpler: Directly set authentication
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "Jai",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    void orderCreatingTest() throws Exception {
        Address address = new Address();
        address.setCity("West virginia");
        address.setCountry("India");
        address.setStreet("&6");
        address.setZipCode("40023");
        address.setId(252L);


        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setRestaurantId(1L);
        createOrderRequest.setDeliveryAddress(address);

        ResponseOrder order = orderService.createOrder(createOrderRequest);

        System.out.println(order);
    }

    @Test
    void testOrderCreationPerformance() throws Exception {
        System.out.println("🚀 Starting performance test...");
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= 5; i++) {
            Address address = new Address();
            address.setCity("City " + i);
            address.setCountry("Country " + i);
            address.setStreet("Street " + i);
            address.setZipCode("1000" + i);

            CreateOrderRequest request = new CreateOrderRequest();
            request.setRestaurantId(1L);
            request.setDeliveryAddress(address);

            ResponseOrder order = orderService.createOrder(request);

            // ✅ Print what you actually get in ResponseOrder
            System.out.println("\n📦 Order " + i + " Created Successfully!");
            System.out.println("   Status: " + order.getOrderStatus());
            System.out.println("   Total Amount: ₹" + order.getTotalAmount());
            System.out.println("   Total Items: " + order.getTotalItem() + " items");
            System.out.println("   Customer: " + order.getCustomerName());
            System.out.println("   Restaurant: " + order.getRestaurantName());
            System.out.println("   Delivery to: " +
                    order.getDeliveryAddress().getStreet() + ", " +
                    order.getDeliveryAddress().getCity());
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("\n📊 Performance Summary:");
        System.out.println("⏱️  Total time for 5 orders: " + totalTime + "ms");
        System.out.println("⏱️  Average time per order: " + (totalTime / 5.0) + "ms");
        System.out.println("✅ All orders placed successfully!");
    }


    @Test
    void upadateorder() throws Exception {
        orderService.updateOrder(2L,"PREPARING");
    }
    @Test
    void deleteorder() throws Exception {
        orderService.deleteOrder(252L);
    }

    @Test
    void getUserAllOrders() {
        for (ResponseOrder userAllOrder : orderService.getUserAllOrders(1L)) {
            System.out.println(userAllOrder);

        }

    }



    @Test
    @Transactional
    void findOrdersByRestaurantIdWithEverything(){
//        for (Order order : orderRepository.findOrdersByRestaurantIdWithEverything(1L)) {
//            System.out.println("Order ID: " + order.getId());
//            System.out.println("Customer: " + order.getCustomer().getUsername());
//            System.out.println("Status: " + order.getOrderStatus());
//            System.out.println("Total Items: " + order.getTotalItem());
//            System.out.println("Items in order: " + order.getItems().size());
//            System.out.println("---");
//        }

        for (ResponseOrder order : orderService.getAllOrdersByRestaurantId(1L)) {
            System.out.println("Order ID: " + order.getId());
            System.out.println("Customer: " + order.getCustomerName());
            System.out.println("Status: " + order.getOrderStatus());
            System.out.println("Total Items: " + order.getTotalItem());
            System.out.println("Items in order: " + order.getItems().size());
            System.out.println("---");
        }


    }







}
