package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class DeliveryAlreadyCompletedException extends DeliveryPartnerException {

    public DeliveryAlreadyCompletedException(Long orderId) {
        super("Delivery already completed for order: " + orderId,
                "DELIVERY_ALREADY_COMPLETED",
                400);
    }
}
