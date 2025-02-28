package com.example.food_court_ms_small_square.domain.exception;

public class InvalidPinException extends RuntimeException {
    public InvalidPinException(String message) {
        super(message);
    }
}
