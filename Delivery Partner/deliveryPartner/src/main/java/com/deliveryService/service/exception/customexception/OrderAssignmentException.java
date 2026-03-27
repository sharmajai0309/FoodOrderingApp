package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class OrderAssignmentException extends DeliveryPartnerException {

    public OrderAssignmentException(String message) {
        super(message,
                "ORDER_ASSIGNMENT_FAILED",
                400);
    }
}