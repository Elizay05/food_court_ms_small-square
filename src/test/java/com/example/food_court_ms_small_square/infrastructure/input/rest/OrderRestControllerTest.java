package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.OrderDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.handler.impl.OrderHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

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

    @Test
    public void test_list_orders_with_default_pagination() {
        // Arrange
        OrderResponseDto orderResponse = new OrderResponseDto();
        List<OrderResponseDto> orderList = List.of(orderResponse);
        PageResponseDto<OrderResponseDto> expectedPage = new PageResponseDto<>(orderList, 1, 1);

        when(orderHandler.listOrdersByFilters(null, 0, 10)).thenReturn(expectedPage);

        // Act
        ResponseEntity<PageResponseDto<OrderResponseDto>> response = orderRestController.listOrders(null, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
        verify(orderHandler).listOrdersByFilters(null, 0, 10);
    }

    @Test
    public void test_list_orders_with_null_status() {
        // Arrange
        OrderResponseDto orderResponse = new OrderResponseDto();
        List<OrderResponseDto> orderList = List.of(orderResponse);
        PageResponseDto<OrderResponseDto> expectedPage = new PageResponseDto<>(orderList, 1, 1);
        String emptyStatus = null;

        when(orderHandler.listOrdersByFilters(emptyStatus, 0, 10)).thenReturn(expectedPage);

        // Act
        ResponseEntity<PageResponseDto<OrderResponseDto>> response = orderRestController.listOrders(emptyStatus, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedPage.getContent(), response.getBody().getContent());
        verify(orderHandler).listOrdersByFilters(emptyStatus, 0, 10);
    }
}
