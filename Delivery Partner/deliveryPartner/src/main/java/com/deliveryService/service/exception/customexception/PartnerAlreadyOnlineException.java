package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class PartnerAlreadyOnlineException extends DeliveryPartnerException {

    public PartnerAlreadyOnlineException(Long id) {
        super("Partner already online: " + id,
                "PARTNER_ALREADY_ONLINE",
                400);
    }
}
