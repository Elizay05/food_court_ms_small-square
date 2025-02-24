package com.example.food_court_ms_small_square.domain.util;

public class DomainConstants {

    private DomainConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String URL_VALIDATE_OWNER = "http://localhost:8080/api/v1/users/validate-owner/{documentNumber}";
    public static final String URL_UPDATE_NIT = "http://localhost:8080/api/v1/users/updateNit/{documentNumber}/{nitRestaurant}";
}
