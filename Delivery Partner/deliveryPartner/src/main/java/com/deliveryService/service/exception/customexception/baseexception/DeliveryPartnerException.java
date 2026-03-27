package com.deliveryService.service.exception.customexception.baseexception;

public class DeliveryPartnerException extends RuntimeException{

    private final String errorCode;
    private final int status;

    public DeliveryPartnerException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() { return errorCode; }
    public int getStatus() { return status; }


}
