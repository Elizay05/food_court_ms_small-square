package com.example.food_court_ms_small_square.domain.spi;

public interface IUserValidationPersistencePort {
    boolean isValidOwner(String documentNumber);
}
