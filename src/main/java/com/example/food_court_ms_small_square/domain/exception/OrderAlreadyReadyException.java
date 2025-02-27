package com.example.food_court_ms_small_square.domain.exception;

public class OrderAlreadyReadyException extends RuntimeException {
    public OrderAlreadyReadyException(String message) {
        super(message);
    }
}
