package com.Food.Service;

import com.Food.Response.PaymentResponse;
import com.Food.Response.ResponseOrder;
import com.Food.dto.OrderPaymentDTO;

public interface PaymentService {

    public PaymentResponse createPaymentLink(OrderPaymentDTO order);

}
