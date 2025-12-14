package com.Food.Service.ServiceImpl;

import com.Food.Response.PaymentResponse;
import com.Food.Response.ResponseOrder;
import com.Food.Service.PaymentService;
import com.Food.dto.OrderPaymentDTO;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.secret.key}")
    private String secretKey;



    /**
     * @param order
     * @return
     */
    @Override
    @Transactional
    public PaymentResponse createPaymentLink(OrderPaymentDTO orderPaymentDTO) {
        Long amountInPaisa = orderPaymentDTO.getAmountInRupees() * 100;

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(
                                "http://localhost:5454/payment/success?orderId=" + orderPaymentDTO.getOrderId()
                        )
                        .setCancelUrl(
                                "http://localhost:5454/payment/cancel?orderId=" + orderPaymentDTO.getOrderId()
                        )
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("inr")
                                                        .setUnitAmount(amountInPaisa)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(orderPaymentDTO.getDescription())
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Error while creating Stripe Session");
        }

        return new PaymentResponse(session.getUrl(), "PAYMENT_CREATED");
    }
}
