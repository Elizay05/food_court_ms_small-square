package com.example.food_court_ms_small_square.application.handler;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;

public interface IOrderHandler {
    OrderResponseDto saveOrder(OrderRequestDto orderRequestDto);
}
