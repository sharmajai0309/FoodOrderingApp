package com.Food.Service.ServiceImpl;

import com.Food.Model.*;
import com.Food.PubSub.OrderEventPublisher;
import com.Food.Repository.IAddressRepository;
import com.Food.Repository.IUserRepository;
import com.Food.Repository.OrderRepository;
import com.Food.Response.ResponseOrder;
import com.Food.Response.UpdateResponseOrder;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.Service.IcartService;
import com.Food.Service.OrderService;
import com.Food.dto.OrderConfirmedEvent;
import com.Food.request.CreateOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

   private final OrderRepository orderRepository;
   private final IAddressRepository iaddressRepository;
   private final IUserRepository iuserRepository;
    private final IUserServices iuserServices;
    private final IResturantService iresturantService;
    private final IcartService cartService;
    // Kafka publisher — publishes ORDER_CONFIRMED when payment succeeds
    private final OrderEventPublisher orderEventPublisher;

    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        return iuserServices.findByUsername(authentication.getName());
    }


    /**
     * @param order
     * @return responseOrder DTO
     */
    @Override
    @Transactional
    public ResponseOrder createOrder(CreateOrderRequest order) throws Exception {
        // Step 1: Get current user
        User user = getUser();
        Long userid = user.getId();

        // Step 2: Handle address
        Address shipAddress = order.getDeliveryAddress();
        Address savedAddress;

        if (shipAddress.getId() != null) {
            // Address exists, fetch from DB
            savedAddress = iaddressRepository.findById(shipAddress.getId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Address not found"));

            //  Set user for the address (if not set)
            if (savedAddress.getUser() == null) {
                savedAddress.setUser(user);
                iaddressRepository.save(savedAddress);
            }
        } else {
            // New address, save it
            // Set user for the new address
            shipAddress.setUser(user);
            savedAddress = iaddressRepository.save(shipAddress);

            // Add to user's addresses list
            if(!user.getAddresses().contains(savedAddress)){
                user.getAddresses().add(savedAddress);
                iuserRepository.save(user);
            }
        }

        // Step 3: Get restaurant
        Restaurant restaurant = iresturantService
                .findRestaurantById(order.getRestaurantId());

        // Step 4: Create order object
        Order createdOrder = new Order();
        createdOrder.setRestaurant(restaurant);
        createdOrder.setCustomer(user);
        createdOrder.setCreatedAt(LocalDateTime.now());
        createdOrder.setDeliveryAddress(savedAddress);
        createdOrder.setOrderStatus(OrderStatus.PENDING);

        // Step 5: Get cart and convert to order items
        Cart cart = cartService.getCartByCustomer(userid);
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()){
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());

            if (cartItem.getIngredients() != null) {
                orderItem.setIngredients(new ArrayList<>(cartItem.getIngredients()));
            } else {
                orderItem.setIngredients(new ArrayList<>());
            }
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalprice(cartItem.getTotalPrize());
            orderItem.setOrder(createdOrder);

            orderItems.add(orderItem); // Directly add to list
        }
        // Step 6: Set order details
        createdOrder.setItems(orderItems);
        createdOrder.setTotalPrice(cart.getTotal());
        createdOrder.setTotalAmount(cart.getTotal());         // Set total amount
        createdOrder.setTotalItem(cart.getItems().size());    // Set item count

        // Step 7: Save order (Cascade.ALL will save order items automatically)
        Order savedOrder = orderRepository.save(createdOrder);
        restaurant.getOrders().add(savedOrder);

        // implement Notification services here

        // Step 8: Clear cart after order creation
        cartService.clearCart(userid);

        // Step 9: Create and return response
        return createResponseOrder(savedOrder);
    }

    /**
     * @param orderId
     * @param orderStatus
     * @return Order
     * @throws Exception
     */



    @Override
    @Transactional
    public UpdateResponseOrder updateOrder(Long orderId, OrderStatus orderStatus) throws Exception {
        // Validate
        if (orderId == null) throw new IllegalArgumentException("Order ID required");


        // Find By OrderID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order #" + orderId + " not found"));

        // Store old status for logging
        OrderStatus oldStatus = order.getOrderStatus();

        // Update
        order.setOrderStatus(orderStatus);

        // Stamp times
        LocalDateTime now = LocalDateTime.now();
        switch (orderStatus) {
            case ACCEPTED -> order.setAcceptedAt(now);
            case PREPARING -> order.setPreparingAt(now);
            case PICKED_UP -> order.setPickedUpAt(now);
            case OUT_FOR_DELIVERY -> order.setOutForDeliveryAt(now);
            case DELIVERED -> order.setDeliveredAt(now);
            default -> {}
        }

        // Save
        Order updatedOrder = orderRepository.save(order);

        // ── Kafka: Publish ORDER_CONFIRMED when payment succeeds ──────────
        // PaymentController calls this with "PAID" after Stripe confirms.
        if (OrderStatus.PAID.equals(orderStatus)) {
            publishOrderConfirmedEvent(updatedOrder);
        }
        // ─────────────────────────────────────────────────────────────────

        // Log
        log.info("Order {} status changed: {} → {}", orderId, oldStatus, orderStatus);

        String notes = switch (orderStatus) {
            case DELIVERED -> "Order delivered successfully at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
            case OUT_FOR_DELIVERY -> "Order is out for delivery. Expected in 30 minutes";
            case PREPARING -> "Restaurant is preparing your order";
            case CANCELLED, PAYMENT_CANCELLED -> "Order cancelled. Refund initiated";
            case PAID -> "Payment Successful. Assigning delivery partner...";
            case PAYMENT_PENDING -> "Payment Pending";
            default -> "Order status updated to " + orderStatus;
        };
        UpdateResponseOrder responseOrder = new UpdateResponseOrder();
        responseOrder.setOrderId(updatedOrder.getId());
        responseOrder.setOrderStatus(orderStatus);
        responseOrder.setNotes(notes);
        // Return
        return responseOrder;
    }


    /**
     * @param orderIs
     */

    @Override
    @Transactional
    public void deleteOrder(Long orderIs) {

        if (orderIs == null) throw new IllegalArgumentException("Order ID required");
        orderRepository.deleteById(orderIs);
        log.info("Order {} has been deleted", orderIs);
    }

    /**
     * @param userId
     * @return
     */


    @Override
    @Transactional(readOnly = true)
    public List<ResponseOrder> getUserAllOrders(Long userId) {
        List<ResponseOrder> results = new ArrayList<>();

        for (Order order : orderRepository.findOrdersWithEverything(userId)) {
            results.add(createResponseOrder(order));
        }
        return results;
    }

    /**
     * @param restaurantId
     * @return
     */



    @Override
    @Transactional(readOnly = true)
    public List<ResponseOrder> getAllOrdersByRestaurantId(Long restaurantId) {
        List<ResponseOrder>  results = new ArrayList<>();
        for (Order order : orderRepository.findOrdersByRestaurantIdWithEverything(restaurantId)) {
            results.add(createResponseOrder(order));
        }
        return results;
    }

    /**
     * @param orderId 
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseOrder getOrderById(Long orderId) {
        Order order = orderRepository.findOrderByIdOnly(orderId).orElseThrow(() ->
                new EntityNotFoundException("Order #" + orderId + " not found"));
        return createResponseOrder(order);
    }


    //helper method
    @Transactional(readOnly = true)
    public ResponseOrder createResponseOrder(Order order) {
        if (order == null) {
            return null; // Or throw exception as per your requirement
        }

        ResponseOrder responseOrder = new ResponseOrder();

        // Basic fields - these should not be null
        responseOrder.setId(order.getId());
        responseOrder.setCreatedAt(order.getCreatedAt());
        responseOrder.setOrderStatus(order.getOrderStatus() != null ? order.getOrderStatus() : OrderStatus.PENDING);
        responseOrder.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : 0L);
        responseOrder.setTotalItem(order.getTotalItem());

        // Tracking fields
        responseOrder.setDeliveryPartnerId(order.getDeliveryPartnerId());
        responseOrder.setAcceptedAt(order.getAcceptedAt());
        responseOrder.setPreparingAt(order.getPreparingAt());
        responseOrder.setPickedUpAt(order.getPickedUpAt());
        responseOrder.setOutForDeliveryAt(order.getOutForDeliveryAt());
        responseOrder.setDeliveredAt(order.getDeliveredAt());

        // Delivery address - handle null
        if (order.getDeliveryAddress() != null) {
            responseOrder.setDeliveryAddress(order.getDeliveryAddress());
        } else {
            // Create empty address or null as per your requirement
            Address emptyAddress = new Address();
            emptyAddress.setCity("Address not available");
            responseOrder.setDeliveryAddress(emptyAddress);
        }

        // Customer info - safe null check
        if (order.getCustomer() != null) {
            responseOrder.setCustomerName(
                    order.getCustomer().getUsername() != null ?
                            order.getCustomer().getUsername() :
                            "Unknown Customer"
            );
        } else {
            responseOrder.setCustomerName("Customer not available");
        }

        // Restaurant info - safe null check with image handling
        if (order.getRestaurant() != null) {
            responseOrder.setRestaurantName(
                    order.getRestaurant().getName() != null ?
                            order.getRestaurant().getName() :
                            "Unknown Restaurant"
            );

            List<String> images = order.getRestaurant().getImages();
            if (images != null && !images.isEmpty()) {
                responseOrder.setRestaurantImage(images.get(0));
            } else {
                responseOrder.setRestaurantImage("");
            }
        } else {
            responseOrder.setRestaurantName("Restaurant not available");
            responseOrder.setRestaurantImage("");
        }

        // Order items - handle null or empty list
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            responseOrder.setItems(order.getItems());
        } else {
            responseOrder.setItems(new ArrayList<>()); // Empty list instead of null
        }

        return responseOrder;
    }

    /**
     * Build a flat address string from Address entity.
     * Used for the Kafka event payload sent to delivery service.
     */
    private String buildAddress(Address address) {
        if (address == null) return "Address not provided";
        return java.util.stream.Stream.of(
                        address.getStreet(),
                        address.getCity(),
                        address.getZipCode()
                )
                .filter(java.util.Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(", "));
    }

    /**
     * Build and publish the ORDER_CONFIRMED Kafka event.
     *
     * Called only when orderStatus == "PAID".
     * Uses the saved Order object — all fields loaded within the same transaction.
     */
    private void publishOrderConfirmedEvent(Order order) {
        try {
            OrderConfirmedEvent event = new OrderConfirmedEvent();
            event.setOrderId(order.getId());
            event.setRestaurantId(
                    order.getRestaurant() != null ? order.getRestaurant().getId() : null);
            event.setRestaurantName(
                    order.getRestaurant() != null ? order.getRestaurant().getName() : "Unknown");
            event.setCustomerName(
                    order.getCustomer() != null ? order.getCustomer().getUsername() : "Unknown");
            event.setCustomerAddress(buildAddress(order.getDeliveryAddress()));
            event.setTotalAmount(order.getTotalAmount());
            event.setStatus("PAID");
            event.setOccurredAt(Instant.now());

            // Async publish — won't block the payment webhook response
            orderEventPublisher.publishOrderConfirmed(event);

        } catch (Exception e) {
            // Safety net: log but never fail the transaction
            log.error("[KAFKA] Error building event for orderId={}: {}", order.getId(), e.getMessage());
        }
    }
}
