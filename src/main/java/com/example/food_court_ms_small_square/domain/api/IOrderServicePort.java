package com.example.food_court_ms_small_square.domain.api;

import com.example.food_court_ms_small_square.domain.model.Order;

public interface IOrderServicePort {
    Order saveOrder(Order order);
}
