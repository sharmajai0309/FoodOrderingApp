package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class UnauthorizedPartnerActionException extends DeliveryPartnerException {

    public UnauthorizedPartnerActionException(String message) {
        super(message,
                "UNAUTHORIZED_PARTNER_ACTION",
                403);
    }
}
