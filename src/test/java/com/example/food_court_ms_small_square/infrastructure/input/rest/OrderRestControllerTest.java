package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.OrderDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.application.handler.impl.OrderHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderRestControllerTest {

    @Mock
    private OrderHandler orderHandler;

    @InjectMocks
    private OrderRestController orderRestController;

    @Test
    public void valid_order_request_returns_created_status() {
        // Arrange
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setNitRestaurante("123456789");
        orderRequestDto.setPlatos(Arrays.asList(new OrderDishRequestDto()));

        OrderResponseDto expectedResponse = new OrderResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setEstado("PENDING");

        when(orderHandler.saveOrder(any(OrderRequestDto.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<OrderResponseDto> response = orderRestController.saveOrder(orderRequestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderHandler).saveOrder(orderRequestDto);
    }
}
