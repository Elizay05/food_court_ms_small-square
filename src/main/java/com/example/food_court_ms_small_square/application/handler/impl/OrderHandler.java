package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.application.handler.IOrderHandler;
import com.example.food_court_ms_small_square.application.mapper.IOrderRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IOrderServicePort;
import com.example.food_court_ms_small_square.domain.model.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;

    @Override
    public OrderResponseDto saveOrder(OrderRequestDto orderRequestDto) {
        Order order = orderRequestMapper.toDomain(orderRequestDto);
        order = orderServicePort.saveOrder(order);
        return orderRequestMapper.toResponseDto(order);
    }
}
