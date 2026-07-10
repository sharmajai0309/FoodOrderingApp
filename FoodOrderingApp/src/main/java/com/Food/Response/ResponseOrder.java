package com.Food.Response;

import com.Food.Model.Address;
import com.Food.Model.OrderItem;
import com.Food.Model.OrderStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class ResponseOrder {

    private Long id;
    private LocalDateTime createdAt;
    private OrderStatus orderStatus;
    private Long totalAmount;
    private int totalItem;
    private Long totalPrice;
    
    // Tracking fields
    private Long deliveryPartnerId;
    private LocalDateTime acceptedAt;
    private LocalDateTime preparingAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime outForDeliveryAt;
    private LocalDateTime deliveredAt;

    // Simple user info
    private String customerName;

    // Restaurant info
    private String restaurantName;
    private String restaurantImage;

    // Delivery address
    private Address deliveryAddress;

    // Order items
    private List<OrderItem> items;

    // Payment Url
    private String paymentUrl;

}
