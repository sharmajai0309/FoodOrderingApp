package com.Food.Service.ServiceImpl;

import com.Food.Metrics.MemoryMonitor;
import com.Food.Model.Cart;
import com.Food.Model.CartItem;
import com.Food.Model.Food;
import com.Food.Model.User;
import com.Food.Repository.CartItemRepository;
import com.Food.Repository.FoodRepository;
import com.Food.Repository.ICartRepository;
import com.Food.Service.IFoodService;
import com.Food.Service.IUserServices;
import com.Food.Service.IcartService;
import com.Food.exceptions.CustomException.UnauthorizedAccessException;
import com.Food.request.AddCartItemRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class CartServiceImpl implements IcartService {

    @Autowired
    private MemoryMonitor memoryMonitor;

    private final ICartRepository iCartRepository;
    private final IUserServices iuserServices;
    private final CartItemRepository cartItemRepository;
    private final IFoodService foodService;
    public CartServiceImpl(ICartRepository iCartRepository, IUserServices iuserServices, CartItemRepository cartItemRepository, IFoodService foodService) {
        this.iCartRepository = iCartRepository;
        this.iuserServices = iuserServices;
        this.cartItemRepository = cartItemRepository;
        this.foodService = foodService;
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return iuserServices.findByUsername(authentication.getName());
    }


    @Override
    @Transactional
    public CartItem addItemToCart(AddCartItemRequest request) throws Exception {
//       authorized User can add Item to only his cart
//        memoryMonitor.suggestGCIfNeeded("addItemToCart");
//        memoryMonitor.logMemoryUsage("Before addItemToCart");

        User currentUser = getCurrentUser();
        long currentUserid = currentUser.getId();
        Long foodId = request.getFoodId();

        Food food = foodService.findfoodById(foodId);
        if (food == null) throw new Exception("Food Not Found with Id : " + foodId);

        Cart cart = iCartRepository.findByCustomerId2(currentUser.getId()).orElseThrow(() -> new EntityNotFoundException("Cart Not Found with this Id : " + currentUser.getId()));

        if (cart == null) {
            throw new Exception("Cart not found for current user");
        }

        if (Objects.equals(cart.getCustomer().getId(), currentUserid)) {
            //if Food is already present
            if(cart.getItems() == null) {
                cart.setItems(new ArrayList<>());
            }


            for (CartItem cartItem : cart.getItems()){
                if(cartItem.getFood().equals(food)){
                    int quantity = cartItem.getQuantity()+request.getQuantity();
                    return updateCartItemQuantity(cartItem.getId(),quantity);
                }
            }
            CartItem newcartItem = new CartItem();
            newcartItem.setFood(food);
            newcartItem.setCart(cart);
            newcartItem.setQuantity(request.getQuantity());
            newcartItem.setIngredients(request.getIngredients());
            newcartItem.setTotalPrize(request.getQuantity() * food.getPrice());
            CartItem savedCartItem = cartItemRepository.save(newcartItem);

            cart.getItems().add(savedCartItem);
            updateCartTotal(cart.getId());
//            memoryMonitor.logMemoryUsage("After addItemToCart");
            return savedCartItem;

        } else {
            throw new UnauthorizedAccessException("You are not authorized to access this cart");
        }

    }

    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + cartItemId));

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrize(cartItem.getFood().getPrice() * quantity);
        CartItem updatedItem = cartItemRepository.save(cartItem);
        Cart cart = cartItem.getCart();
        updateCartTotal(cart.getId());

        return updatedItem;
    }


    @Override
    @Transactional
    public void removeItemFromCart(Long cartItemId) throws Exception {
        long currentUserid = getCurrentUser().getId();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        Cart cart = cartItem.getCart();

        if (cart == null) {
            throw new IllegalStateException("Cart not found for cart item");
        }

        if (Objects.equals(currentUserid, cart.getCustomer().getId())) {
            // Removing item
            if (cart.getItems() != null) {
                cart.getItems().remove(cartItem);
            }

            cartItemRepository.delete(cartItem);
            updateCartTotal(cart.getId());
        } else {
            log.info("U can delete from only your cart");
            throw new AccessDeniedException("U can delete from only your cart");
        }
    }


    @Override
    @Transactional
    public CartItem updateCartItemIngredients(Long cartItemId, List<String> newIngredients) throws Exception {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with :" + cartItemId));

        Cart cart = cartItem.getCart();

        List<String> oldingredients = cartItem.getIngredients();
        if (oldingredients == null) oldingredients = new ArrayList<>();

        for (String ingredient : newIngredients) {
            if (!oldingredients.contains(ingredient)) {
                oldingredients.add(ingredient);
            }
        }

        cartItem.setIngredients(oldingredients);
        CartItem updatedItem = cartItemRepository.save(cartItem);


        return updatedItem;
    }

    @Override
    @Transactional(readOnly = true)
    public Cart getCartByCustomer(Long userId) throws Exception {

        long id = getCurrentUser().getId();
        Cart cart = iCartRepository.findByCustomerId2(userId).orElseThrow(() -> new EntityNotFoundException("cart not found with this User id : " + id));
        if (cart == null) {
            throw new Exception("Cart is empty");
        }
        return cart;


    }

    @Override
    @Transactional(readOnly = true)
    public Cart getCartById(Long cartId) throws Exception {
        return iCartRepository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("No Cart found for Id : " + cartId));
    }

    @Override
    @Transactional
    public Cart clearCart(Long customerId) throws Exception {
        if(Objects.equals(getCurrentUser().getId(), customerId)){
            Cart cart = iCartRepository.findByCustomerId2(customerId)
                    .orElseThrow(() -> new EntityNotFoundException("no cart found"));
            if(cart.getItems() != null && !cart.getItems().isEmpty()) {
                List<CartItem> itemsToRemove = new ArrayList<>(cart.getItems());
                for (CartItem item : itemsToRemove) {
                    cart.getItems().remove(item);
                }
            }

            cart.setTotal(0L);
            return iCartRepository.save(cart);
        }
        else {
            throw new AccessDeniedException("You can only clear your own cart");
        }
    }

    @Override
    @Transactional
    public Long calculateCartTotal(Cart cart) throws Exception {
        if (cart == null) {
            return 0L;
        }
        Cart cartWithFood = iCartRepository.findCartWithItemsAndFood(cart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        if (cartWithFood.getItems() == null) {
            return 0L;
        }
        Long total = 0L;
        for (CartItem item : cartWithFood.getItems()) {
            total += item.getFood().getPrice() * item.getQuantity();
        }
        return total;
    }

    @Override
    @Transactional
    public Cart updateCartTotal(Long cartId) throws Exception {

        Cart cart = iCartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Long total = 0L;
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                total += item.getTotalPrize();
            }
        }

        cart.setTotal(total);
        return iCartRepository.save(cart);
    }

    private void refreshCartTotal(Cart cart) {
        try {
            updateCartTotal(cart.getId());
        } catch (Exception e) {
            // Log error but don't throw
            System.err.println("Error updating cart total: " + e.getMessage());
        }
    }
}
