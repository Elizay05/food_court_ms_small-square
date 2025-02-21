package com.example.food_court_ms_small_square.domain.exception;

public class RestaurantAlreadyExists extends RuntimeException {
    public RestaurantAlreadyExists(String message) {
        super(message);
    }
}
