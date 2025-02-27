package com.example.food_court_ms_small_square.domain.spi;

public interface IMessagePersistencePort {
    void sendOrderReadyMessage(String phoneNumber, String message);
}
