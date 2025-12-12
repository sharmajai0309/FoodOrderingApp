package com.Food.Service;

import com.Food.Model.Order;
import com.Food.Model.User;
import com.Food.Response.ApiResponse;
import com.Food.Response.ResponseOrder;
import com.Food.Response.UpdateResponseOrder;
import com.Food.request.CreateOrderRequest;

import java.util.List;

public interface OrderService {

    public ResponseOrder createOrder(CreateOrderRequest order) throws Exception;

    public UpdateResponseOrder updateOrder(Long orderId , String orderStatus) throws Exception;

    public void deleteOrder(Long orderIs);

    public List<ResponseOrder> getUserAllOrders(Long userId);

    public List<ResponseOrder> getAllOrdersByRestaurantId(Long restaurantId);

    public ResponseOrder getOrderById(Long orderId);


}
