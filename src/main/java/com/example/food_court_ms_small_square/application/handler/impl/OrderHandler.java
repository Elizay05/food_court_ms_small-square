package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.handler.IOrderHandler;
import com.example.food_court_ms_small_square.application.mapper.IOrderRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IOrderServicePort;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;

    @Override
    public OrderResponseDto saveOrder(OrderRequestDto orderRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Order order = orderRequestMapper.toDomain(orderRequestDto);
        order = orderServicePort.saveOrder(order, userDetails.getDocumentNumber());
        return orderRequestMapper.toResponseDto(order);
    }

    @Override
    public PageResponseDto<OrderResponseDto> listOrdersByFilters (String estado, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        PageRequestDto pageRequestDto = new PageRequestDto(page, size, "id", true);

        Page<Order> orderPage = orderServicePort.listOrdersByFilters(estado, pageRequestDto, userDetails.getNit());

        List<OrderResponseDto> orderDtos = orderPage.getContent().stream()
                .map(orderRequestMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(orderDtos, orderPage.getTotalPages(), orderPage.getTotalElements());
    }

    @Override
    public OrderResponseDto assignOrder(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Order order = orderServicePort.assignOrder(orderId, userDetails.getDocumentNumber(), userDetails.getNit());
        return orderRequestMapper.toResponseDto(order);
    }

    @Override
    public OrderResponseDto readyOrder(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Order order = orderServicePort.readyOrder(orderId, userDetails.getNit());
        return orderRequestMapper.toResponseDto(order);
    }

    @Override
    public OrderResponseDto deliveredOrder(Long orderId, String pin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Order order = orderServicePort.deliveredOrder(orderId, pin, userDetails.getNit());
        return orderRequestMapper.toResponseDto(order);
    }
}
