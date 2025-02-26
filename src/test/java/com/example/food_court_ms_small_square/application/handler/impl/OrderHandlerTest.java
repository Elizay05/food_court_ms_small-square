package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.mapper.IOrderRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IOrderServicePort;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    public void test_save_order_success() {
        // Arrange
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setNitRestaurante("123456789");
        orderRequestDto.setPlatos(new ArrayList<>());

        Order order = new Order(1L, "DOC123", "123456789", LocalDateTime.now(), "PENDING", null, new ArrayList<>());
        Order savedOrder = new Order(1L, "DOC123", "123456789", LocalDateTime.now(), "PENDING", null, new ArrayList<>());

        CustomUserDetails userDetails = new CustomUserDetails("user1", "DOC123", null, new ArrayList<>());
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(orderRequestMapper.toDomain(orderRequestDto)).thenReturn(order);
        when(orderServicePort.saveOrder(order, "DOC123")).thenReturn(savedOrder);
        when(orderRequestMapper.toResponseDto(savedOrder)).thenReturn(new OrderResponseDto());

        // Act
        OrderResponseDto result = orderHandler.saveOrder(orderRequestDto);

        // Assert
        assertNotNull(result);
        verify(orderRequestMapper).toDomain(orderRequestDto);
        verify(orderServicePort).saveOrder(order, "DOC123");
        verify(orderRequestMapper).toResponseDto(savedOrder);
    }

    @Test
    public void test_list_orders_returns_valid_page_response() {
        // Arrange
        IOrderServicePort orderServicePort = mock(IOrderServicePort.class);
        IOrderRequestMapper orderRequestMapper = mock(IOrderRequestMapper.class);
        OrderHandler orderHandler = new OrderHandler(orderServicePort, orderRequestMapper);

        Authentication auth = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails("user1", "doc1", "nit1", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);

        Order order1 = new Order(1L, "111", "nit1", LocalDateTime.now(), "PENDING", null, new ArrayList<>());
        Order order2 = new Order(2L, "222", "nit1", LocalDateTime.now(), "PENDING", null, new ArrayList<>());
        List<Order> orders = Arrays.asList(order1, order2);
        Page<Order> orderPage = new Page<>(orders, 1, 2);

        OrderResponseDto orderDto1 = new OrderResponseDto();
        OrderResponseDto orderDto2 = new OrderResponseDto();
        when(orderServicePort.listOrdersByFilters(eq("PENDING"), any(PageRequestDto.class), eq("nit1")))
                .thenReturn(orderPage);
        when(orderRequestMapper.toResponseDto(order1)).thenReturn(orderDto1);
        when(orderRequestMapper.toResponseDto(order2)).thenReturn(orderDto2);

        // Act
        PageResponseDto<OrderResponseDto> result = orderHandler.listOrdersByFilters("PENDING", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void test_list_orders_handles_empty_result() {
        // Arrange
        IOrderServicePort orderServicePort = mock(IOrderServicePort.class);
        IOrderRequestMapper orderRequestMapper = mock(IOrderRequestMapper.class);
        OrderHandler orderHandler = new OrderHandler(orderServicePort, orderRequestMapper);

        Authentication auth = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails("user1", "doc1", "nit1", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);

        Page<Order> emptyPage = new Page<>(Collections.emptyList(), 0, 0);
        when(orderServicePort.listOrdersByFilters(eq("PENDING"), any(PageRequestDto.class), eq("nit1")))
                .thenReturn(emptyPage);

        // Act
        PageResponseDto<OrderResponseDto> result = orderHandler.listOrdersByFilters("PENDING", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getContent().isEmpty());
    }
}
