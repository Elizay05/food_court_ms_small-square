package com.example.food_court_ms_small_square.domain.exception;

public class OrderNotAssignedException extends RuntimeException {
    public OrderNotAssignedException(String message) {
        super(message);
    }
}
