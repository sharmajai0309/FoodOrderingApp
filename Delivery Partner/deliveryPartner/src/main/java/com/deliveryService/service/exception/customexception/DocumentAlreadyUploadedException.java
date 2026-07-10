package com.deliveryService.service.exception.customexception;

import com.deliveryService.service.exception.customexception.baseexception.DeliveryPartnerException;

public class DocumentAlreadyUploadedException extends DeliveryPartnerException {

    public DocumentAlreadyUploadedException(String documentType) {
        super(documentType + " has already been uploaded.", "DOCUMENT_ALREADY_UPLOADED", 400);
    }
}
