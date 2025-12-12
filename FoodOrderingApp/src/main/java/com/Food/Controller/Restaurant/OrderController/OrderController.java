package com.Food.Controller.Restaurant.OrderController;

import com.Food.Model.User;
import com.Food.Response.ApiResponse;
import com.Food.Response.ResponseOrder;
import com.Food.Response.UpdateResponseOrder;
import com.Food.Service.IUserServices;
import com.Food.Service.OrderService;
import com.Food.request.CreateOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("v1/api/customer/order")
public class OrderController {
    private final OrderService orderService;
    private final IUserServices userServices;

    protected User getCurrentUserId(){
        return userServices.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ResponseOrder>> createOrder(
            @Valid @RequestBody CreateOrderRequest orderRequest) {
        try {
            ResponseOrder responseOrder = orderService.createOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(responseOrder, "Order created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create order: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_ADMIN')")
    public ResponseEntity<ApiResponse<UpdateResponseOrder>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        try {
            UpdateResponseOrder updatedOrder = orderService.updateOrder(orderId, status);
            return ResponseEntity.ok(ApiResponse.success(updatedOrder, "Order status updated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update order status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete order: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<ResponseOrder>>> getUserOrders() {
        try {
            Long userId = getCurrentUserId().getId();
            List<ResponseOrder> orders = orderService.getUserAllOrders(userId);
            return ResponseEntity.ok(ApiResponse.success(orders, "User orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user orders: " + e.getMessage()));
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<ResponseOrder>>> getRestaurantOrders(
            @PathVariable Long restaurantId) {
        try {
            List<ResponseOrder> orders = orderService.getAllOrdersByRestaurantId(restaurantId);
            return ResponseEntity.ok(ApiResponse.success(orders, "Restaurant orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve restaurant orders: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ResponseOrder>> getOrderById(@PathVariable Long orderId) {
        try {
            orderService.getOrderById(orderId);
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error("Endpoint not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve order: " + e.getMessage()));
        }
    }
}