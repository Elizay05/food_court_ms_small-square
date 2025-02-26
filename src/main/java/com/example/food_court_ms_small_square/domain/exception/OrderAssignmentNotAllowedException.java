package com.example.food_court_ms_small_square.domain.exception;

public class OrderAssignmentNotAllowedException extends RuntimeException {
    public OrderAssignmentNotAllowedException(String message) {
        super(message);
    }
}
