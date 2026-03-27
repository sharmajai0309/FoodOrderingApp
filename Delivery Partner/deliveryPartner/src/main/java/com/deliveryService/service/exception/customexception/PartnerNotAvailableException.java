package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class PartnerNotAvailableException extends DeliveryPartnerException {

    public PartnerNotAvailableException(Long id) {
        super("Partner is offline or unavailable: " + id,
                "PARTNER_NOT_AVAILABLE",
                400);
    }
}
