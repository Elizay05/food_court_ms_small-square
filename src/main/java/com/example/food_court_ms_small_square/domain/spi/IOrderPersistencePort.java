package com.example.food_court_ms_small_square.domain.spi;

import com.example.food_court_ms_small_square.domain.model.Order;

import java.util.List;

public interface IOrderPersistencePort {
    Order saveOrder(Order order);
    boolean areDishesValidForRestaurant(List<Long> dishIds, String nitRestaurante);
    boolean hasActiveOrders(String clienteId);
}
