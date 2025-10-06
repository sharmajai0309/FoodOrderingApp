package com.Food.exceptions.CustomException;

public class UnauthorizedAccessException extends Exception{
    public UnauthorizedAccessException(String message) {
        super(message);
    }

}
