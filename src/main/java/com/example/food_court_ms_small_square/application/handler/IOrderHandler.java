package com.example.food_court_ms_small_square.application.handler;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;

public interface IOrderHandler {
    OrderResponseDto saveOrder(OrderRequestDto orderRequestDto);
    PageResponseDto<OrderResponseDto> listOrdersByFilters(String estado, int page, int size);
    OrderResponseDto assignOrder(Long orderId);
    OrderResponseDto readyOrder(Long orderId);
    OrderResponseDto deliveredOrder(Long orderId, String pin);
}
