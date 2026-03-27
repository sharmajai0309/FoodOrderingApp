package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class InvalidRequestException extends DeliveryPartnerException {

    public InvalidRequestException(String message) {
        super(message, "INVALID_REQUEST", 400);
    }
}
