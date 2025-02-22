package com.example.food_court_ms_small_square.domain.exception;

public class ElementAlreadyExists extends RuntimeException {
    public ElementAlreadyExists(String message) {
        super(message);
    }
}
