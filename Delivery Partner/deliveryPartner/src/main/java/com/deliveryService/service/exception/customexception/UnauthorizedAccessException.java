package com.deliveryService.service.exception.customexception;

public class UnauthorizedAccessException extends Exception{
    public UnauthorizedAccessException(String message) {
        super(message);
    }

}
