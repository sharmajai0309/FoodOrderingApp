package com.Food.Service;

import com.Food.Model.Cart;
import com.Food.Model.CartItem;
import com.Food.request.AddCartItemRequest;


import java.util.List;


public interface IcartService {

    // Cart item methods
    public CartItem addItemToCart(AddCartItemRequest request) throws Exception;

    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception;

    public void removeItemFromCart(Long cartItemId) throws Exception;

    public CartItem updateCartItemIngredients(Long cartItemId, List<String> ingredients) throws Exception;

    // Cart related methods
    public Cart getCartByCustomer(Long UserId) throws Exception;

    public Cart getCartById(Long cartId) throws Exception;

    public Cart clearCart(Long customerId) throws Exception;

    public Long calculateCartTotal(Cart cart) throws Exception;

    public Cart updateCartTotal(Long cartId) throws Exception;


    //  Utility methods
//    public List<CartItem> getAllCartItems(Long cartId) throws Exception;
//
//    public int getCartItemsCount(Long customerId) throws Exception;
//
//    public boolean isCartEmpty(Long customerId) throws Exception;
//
//    public Cart createCartForCustomer(Long customerId) throws Exception;



}
