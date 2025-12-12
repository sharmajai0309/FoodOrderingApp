package com.Food.Controller.Restaurant.CartController;


import com.Food.Model.Cart;
import com.Food.Model.CartItem;
import com.Food.Response.ApiResponse;
import com.Food.Service.IcartService;
import com.Food.request.AddCartItemIngredientsRequest;
import com.Food.request.AddCartItemQuantityRequest;
import com.Food.request.AddCartItemRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Customer/Cart")
@PreAuthorize("isAuthenticated()")
public class CartController {


    private final IcartService icartService;


    @PutMapping("/add-item")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestBody AddCartItemRequest request) {
        try {
            CartItem cartItem = icartService.addItemToCart(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(cartItem, "Item added to cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to add item to cart: " + e.getMessage()));
        }
    }

    @PutMapping("/updateCartItem")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse>UpdateCartItemQuantity(@RequestBody AddCartItemQuantityRequest request){
        CartItem cartItem;
        try {
            cartItem = icartService.updateCartItemQuantity(request.getCartItemId(), request.getQuantity());
        } catch (Exception e) {
            throw new EntityNotFoundException(e);
        }
        return ResponseEntity.ok(
                ApiResponse.success(cartItem, "Cart item quantity updated successfully"));
    }


    @DeleteMapping("/delete/{cartItemId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse>removeItemFromCart(@PathVariable Long cartItemId) throws Exception {
        icartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok(ApiResponse.success("cartItem deleted"));
    }


    @PutMapping("/updateCartItemIngredients")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse>updateCartItemIngredients(@RequestBody AddCartItemIngredientsRequest request) throws Exception {
        CartItem cartItem = icartService.updateCartItemIngredients(request.getCartItemId(), request.getIngredients());
        return ResponseEntity.ok(ApiResponse.success(cartItem,"Cart Updated with new Ingredients"));
    }

    @GetMapping("/cartId/{cartId}")
    public ResponseEntity<ApiResponse>getCartByCustomer(@PathVariable Long cartId) throws Exception {
        Cart cart = icartService.getCartByCustomer(cartId);
        List<CartItem> items = cart.getItems();
        Long total = cart.getTotal();
        ApiResponse<String> ReceivedCart = ApiResponse.success("cartItems : " + items + "CartTotal : " + total, "Cart");
        return ResponseEntity.ok(ReceivedCart);
    }

    @DeleteMapping("/clear-Cart/{cartId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse>clearCart(@PathVariable Long cartId) throws Exception {
        icartService.clearCart(cartId);
        return ResponseEntity.ok(ApiResponse.success("Cart Cleared"));
    }


    @GetMapping("/Get-cart/{cartId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse>getCartById(@PathVariable Long cartId) throws Exception {
        Cart cartById = icartService.getCartById(cartId);
        return ResponseEntity.ok(ApiResponse.success(cartById,"Cart Fetched By id : "+cartId));
    }



}
