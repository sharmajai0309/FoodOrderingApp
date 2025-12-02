package com.Food.Service.ServiceImpl;

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
import com.Food.request.AddcartItemRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CartServiceImpl implements IcartService {


    private ICartRepository iCartRepository;
    private IUserServices iuserServices;
    private CartItemRepository cartItemRepository;
    private IFoodService foodService;


    public CartServiceImpl(ICartRepository iCartRepository, IUserServices iuserServices, CartItemRepository cartItemRepository, IFoodService foodService) {
        this.iCartRepository = iCartRepository;
        this.iuserServices = iuserServices;
        this.cartItemRepository = cartItemRepository;
        this.foodService = foodService;
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("No authentication found");
        }
        Object principal = authentication.getPrincipal();
        String principalValue = principal.toString();
        try {
            Long userId = Long.parseLong(principalValue);
            User user = iuserServices.findById(userId);
            if (user != null) {
                return user;
            }
        } catch (NumberFormatException ignored) {

        }


        throw new RuntimeException("User not found for principal: " + principalValue);
    }


    @Override
    @Transactional
    public CartItem addItemToCart(AddcartItemRequest request) throws Exception {
//       authorized User can add Item to only his cart

        User currentUser = getCurrentUser();
        long currentUserid = currentUser.getId();
        Long foodId = request.getFoodId();

        Food food = foodService.findfoodById(foodId);
        if(food == null) throw new Exception("Food Not Found with Id : " + foodId);

        Cart cart = iCartRepository.findByCustomerId2(currentUser.getId());

        if (cart == null) {
            throw new Exception("Cart not found for current user");
        }

        if (Objects.equals(cart.getCustomer().getId(), currentUserid)) {
                  //if Food is already present
            if(cart.getItems() == null ) cart.setItems(new ArrayList<>());


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
        return cartItem;
    }



    @Override
    @Transactional
    public void removeItemFromCart(Long cartItemId) throws Exception {

        long CurrentUserid = getCurrentUser().getId();
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + cartItemId));
        Cart cart = cartItem.getCart();

        if(Objects.equals(CurrentUserid,cart.getCustomer().getId())){

            // 1. Saving Item prize
            long itemPrice = cartItem.getTotalPrize(); // ₹300

            // 2. Remove item from cart's list
            cart.getItems().remove(cartItem);

            // 3. Update cart total
            long newCartTotal = cart.getTotal() - itemPrice; // ₹1000 - ₹300 = ₹700
            cart.setTotal(newCartTotal);

            // 4. Save cart with updated total
            iCartRepository.save(cart);

            // 5. Delete item (cascade in action)
            cartItemRepository.delete(cartItem);

        }



    }


    @Override
    public CartItem updateCartItemIngredients(Long cartItemId, List<String> ingredients) throws Exception {
        return null;
    }

    @Override
    public Cart getCartByCustomer(Long userId) throws Exception {
        return null;
    }



    @Override
    public Cart getCartById(Long cartId) throws Exception {
        return null;
    }

    @Override
    public Cart clearCart(Long customerId) throws Exception {
        return null;
    }

    @Override
    public Long calculateCartTotal(Cart cart) throws Exception {
        return 0L;
    }

    @Override
    public Cart updateCartTotal(Long cartId) throws Exception {
        return null;
    }
}
