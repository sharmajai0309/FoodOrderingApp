package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class PartnerNotFoundException extends DeliveryPartnerException {

    public PartnerNotFoundException(Long id) {
        super("Delivery partner not found with id : " +id,"Delivery_Partner_NOT_FOUND",404);
    }
}
