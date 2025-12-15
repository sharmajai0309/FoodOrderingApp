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
    public PaymentResponse createPaymentLink(OrderPaymentDTO dto) {

        Long amountInPaisa = dto.getAmountInRupees() * 100;

        if (amountInPaisa <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:5454/payment/success?orderId=" + dto.getOrderId())
                        .setCancelUrl("http://localhost:5454/payment/cancel?orderId=" + dto.getOrderId())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("inr")
                                                        .setUnitAmount(amountInPaisa)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Food Order #" + dto.getOrderId())
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        try {
            Session session = Session.create(params);
            return new PaymentResponse(session.getUrl(), "PAYMENT_CREATED");
        } catch (StripeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
