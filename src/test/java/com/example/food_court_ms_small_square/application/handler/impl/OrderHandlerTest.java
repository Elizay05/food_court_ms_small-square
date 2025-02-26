package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.application.mapper.IOrderRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IOrderServicePort;
import com.example.food_court_ms_small_square.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderHandlerTest {

    @Mock
    private IOrderServicePort orderServicePort;

    @Mock
    private IOrderRequestMapper orderRequestMapper;

    @InjectMocks
    private OrderHandler orderHandler;

    @Test
    public void test_save_order_maps_dto_to_domain_successfully() {
        // Arrange
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setNitRestaurante("123");
        orderRequestDto.setPlatos(new ArrayList<>());

        Order expectedOrder = new Order(null, null, "123", null, null, null, new ArrayList<>());
        Order savedOrder = new Order(1L, "client1", "123", LocalDateTime.now(), "PENDING", null, new ArrayList<>());
        OrderResponseDto expectedResponse = new OrderResponseDto(1L, "PENDING", LocalDateTime.now(), new ArrayList<>());

        when(orderRequestMapper.toDomain(orderRequestDto)).thenReturn(expectedOrder);
        when(orderServicePort.saveOrder(expectedOrder)).thenReturn(savedOrder);
        when(orderRequestMapper.toResponseDto(savedOrder)).thenReturn(expectedResponse);

        // Act
        OrderResponseDto result = orderHandler.saveOrder(orderRequestDto);

        // Assert
        verify(orderRequestMapper).toDomain(orderRequestDto);
        verify(orderServicePort).saveOrder(expectedOrder);
        verify(orderRequestMapper).toResponseDto(savedOrder);
        assertEquals(expectedResponse, result);
    }
}
