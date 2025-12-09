package com.Food.Service.ServiceImpl;

import com.Food.Model.*;
import com.Food.Repository.IAddressRepository;
import com.Food.Repository.IUserRepository;
import com.Food.Repository.OrderItemRepository;
import com.Food.Repository.OrderRepository;
import com.Food.Response.ResponseOrder;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.Service.IcartService;
import com.Food.Service.OrderService;
import com.Food.request.CreateOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.border.Border;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.Food.Metrics.MemoryMonitor.log;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {


   private  final OrderRepository orderRepository;
   private final OrderItemRepository orderItemRepository;
   private final IAddressRepository iaddressRepository;
   private final IUserRepository iuserRepository;
    private final IUserServices iuserServices;
    private final IResturantService iresturantService;
    private final IcartService cartService;

    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        return iuserServices.findByUsername(authentication.getName());
    }


    /**
     * @param CreateOrderRequest
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

            // ✅ Set user for the address (if not set)
            if (savedAddress.getUser() == null) {
                savedAddress.setUser(user);
                iaddressRepository.save(savedAddress);
            }
        } else {
            // New address, save it
            // ✅ Set user for the new address
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
        createdOrder.setOrderStatus("PENDING");

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
    public ResponseOrder updateOrder(Long orderId, String orderStatus) throws Exception {
        // Validate
        if (orderId == null) throw new IllegalArgumentException("Order ID required");
        if (orderStatus == null || orderStatus.trim().isEmpty())
            throw new IllegalArgumentException("Order status required");

        // Find
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order #" + orderId + " not found"));

        // Store old status for logging
        String oldStatus = order.getOrderStatus();

        // Update
        order.setOrderStatus(orderStatus);

        // Save
        Order updatedOrder = orderRepository.save(order);

        // Log
        log.info("Order {} status changed: {} → {}", orderId, oldStatus, orderStatus);

        // Return
        return createResponseOrder(updatedOrder);
    }





    /**
     * @param orderIs
     */

    @Override
    public void deleteOrder(Long orderIs) {

    }

    /**
     * @param userId
     * @return
     */
    @Override
    public List<Order> getUserAllOrders(Long userId) {
        return List.of();
    }

    /**
     * @param restaurantId
     * @return
     */
    @Override
    public List<Order> getAllOrdersByRestaurantId(Long restaurantId) {
        return List.of();
    }



    //helper method
    @Transactional
        private ResponseOrder createResponseOrder(Order order) {
            if (order == null) {
                return null; // Or throw exception as per your requirement
            }

            ResponseOrder responseOrder = new ResponseOrder();

            // Basic fields - these should not be null
            responseOrder.setCreatedAt(order.getCreatedAt());
            responseOrder.setOrderStatus(order.getOrderStatus() != null ? order.getOrderStatus() : "UNKNOWN");
            responseOrder.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : 0L);
            responseOrder.setTotalItem(order.getTotalItem() != null ? order.getTotalItem() : 0);

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

                // Handle restaurant images (could be String, List, or null)
                Object images = order.getRestaurant().getImages();
                if (images != null) {
                    responseOrder.setRestaurantImage(images.toString());
                } else {
                    responseOrder.setRestaurantImage("No image available");
                }
            } else {
                responseOrder.setRestaurantName("Restaurant not available");
                responseOrder.setRestaurantImage("No image available");
            }

            // Order items - handle null or empty list
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                responseOrder.setItems(order.getItems());
            } else {
                responseOrder.setItems(new ArrayList<>()); // Empty list instead of null
            }

            return responseOrder;
        }
}
































