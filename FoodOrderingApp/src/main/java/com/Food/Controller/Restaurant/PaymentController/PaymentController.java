package com.Food.Controller.Restaurant.PaymentController;

import com.Food.Service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@AllArgsConstructor
public class PaymentController {

    private final OrderService orderService;


    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam Long orderId) throws Exception {


        orderService.updateOrder(orderId, "PAID");

        return ResponseEntity.ok("Payment successful for order " + orderId);
    }

    //  PAYMENT CANCEL
    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel(
            @RequestParam Long orderId) throws Exception {

        orderService.updateOrder(orderId, "PAYMENT_CANCELLED");

        return ResponseEntity.ok("Payment cancelled for order " + orderId);
    }
}

