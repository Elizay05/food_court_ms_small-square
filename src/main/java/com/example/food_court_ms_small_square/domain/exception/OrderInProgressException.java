package com.example.food_court_ms_small_square.domain.exception;

public class OrderInProgressException extends RuntimeException {
    public OrderInProgressException(String message) {
        super(message);
    }
}
