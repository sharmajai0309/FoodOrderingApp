package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class OrderAlreadyPickedException extends DeliveryPartnerException {
    public OrderAlreadyPickedException(Long orderId) {
        super("Order already picked by another partner: " + orderId,
                "ORDER_ALREADY_PICKED",
                400);
    }
}
