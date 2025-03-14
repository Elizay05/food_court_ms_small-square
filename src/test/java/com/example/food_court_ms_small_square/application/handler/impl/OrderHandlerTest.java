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

        Order order = new Order(1L, "DOC123", "123456789", LocalDateTime.now(), "PENDING", null, "123456", new ArrayList<>());
        Order savedOrder = new Order(1L, "DOC123", "123456789", LocalDateTime.now(), "PENDING", null, "123456", new ArrayList<>());

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

        Order order1 = new Order(1L, "111", "nit1", LocalDateTime.now(), "PENDING", null, "123456", new ArrayList<>());
        Order order2 = new Order(2L, "222", "nit1", LocalDateTime.now(), "PENDING", null, "123456", new ArrayList<>());
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

    @Test
    public void test_assign_order_success() {
        // Arrange
        Long orderId = 1L;
        String documentNumber = "12345";
        String nit = "67890";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", documentNumber, nit, Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        Order mockOrder = new Order(orderId, "client1", nit, LocalDateTime.now(), "ASSIGNED", documentNumber,"123456", Collections.emptyList());
        OrderResponseDto expectedResponse = new OrderResponseDto(orderId, "client1", nit, LocalDateTime.now(), "ASSIGNED", documentNumber, "025663", Collections.emptyList());

        when(orderServicePort.assignOrder(orderId, documentNumber, nit)).thenReturn(mockOrder);
        when(orderRequestMapper.toResponseDto(mockOrder)).thenReturn(expectedResponse);

        // Act
        OrderResponseDto result = orderHandler.assignOrder(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(documentNumber, result.getIdChef());
        assertEquals("ASSIGNED", result.getEstado());

        verify(orderServicePort).assignOrder(orderId, documentNumber, nit);
        verify(orderRequestMapper).toResponseDto(mockOrder);
    }

    @Test
    public void test_assign_order_null_id() {
        // Arrange
        Long orderId = null;
        String documentNumber = "12345";
        String nit = "67890";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", documentNumber, nit, Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderServicePort.assignOrder(orderId, documentNumber, nit))
                .thenThrow(new IllegalArgumentException("Order ID cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderHandler.assignOrder(orderId);
        });

        verify(orderServicePort).assignOrder(orderId, documentNumber, nit);
        verify(orderRequestMapper, never()).toResponseDto((Order) any());
    }

    @Test
    public void test_ready_order_success() {
        // Arrange
        Long orderId = 1L;
        String nit = "123456789";

        OrderHandler orderHandler = new OrderHandler(orderServicePort, orderRequestMapper);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "doc123", nit, Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        Order order = new Order(orderId, "client1", nit, LocalDateTime.now(), "READY", "chef1","123456", Collections.emptyList());
        OrderResponseDto expectedResponse = new OrderResponseDto(orderId, "client1", nit, LocalDateTime.now(), "READY", "chef1", "025663", Collections.emptyList());

        when(orderServicePort.readyOrder(orderId, nit)).thenReturn(order);
        when(orderRequestMapper.toResponseDto(order)).thenReturn(expectedResponse);

        // Act
        OrderResponseDto result = orderHandler.readyOrder(orderId);

        // Assert
        assertEquals(expectedResponse, result);
        verify(orderServicePort).readyOrder(orderId, nit);
        verify(orderRequestMapper).toResponseDto(order);
    }

    @Test
    public void test_ready_order_null_id() {
        // Arrange
        Long orderId = null;
        String nit = "123456789";

        OrderHandler orderHandler = new OrderHandler(orderServicePort, orderRequestMapper);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "doc123", nit, Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderServicePort.readyOrder(orderId, nit)).thenThrow(new IllegalArgumentException("Order ID cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderHandler.readyOrder(orderId);
        });

        verify(orderServicePort).readyOrder(orderId, nit);
        verify(orderRequestMapper, never()).toResponseDto((Order) any());
    }

    @Test
    public void test_delivered_order_success() {
        // Arrange
        Long orderId = 1L;
        String pin = "1234";
        String nit = "123456789";

        Order order = new Order(orderId, "client1", nit, LocalDateTime.now(), "DELIVERED", "chef1", pin, new ArrayList<>());
        OrderResponseDto expectedResponse = new OrderResponseDto(orderId, "client1", nit, LocalDateTime.now(), "DELIVERED", "chef1", pin, new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user1", "doc1", nit, new ArrayList<>());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderServicePort.deliveredOrder(orderId, pin, nit)).thenReturn(order);
        when(orderRequestMapper.toResponseDto(order)).thenReturn(expectedResponse);

        // Act
        OrderResponseDto result = orderHandler.deliveredOrder(orderId, pin);

        // Assert
        assertEquals(expectedResponse, result);
        verify(orderServicePort).deliveredOrder(orderId, pin, nit);
        verify(orderRequestMapper).toResponseDto(order);
    }

    @Test
    public void test_delivered_order_null_id() {
        // Arrange
        Long orderId = null;
        String pin = "1234";
        String nit = "123456789";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user1", "doc1", nit, new ArrayList<>());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderServicePort.deliveredOrder(orderId, pin, nit)).thenThrow(new IllegalArgumentException("Order ID cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderHandler.deliveredOrder(orderId, pin);
        });

        verify(orderServicePort).deliveredOrder(orderId, pin, nit);
        verifyNoInteractions(orderRequestMapper);
    }

    @Test
    public void test_cancel_order_success() {
        // Arrange
        Long orderId = 1L;
        String documentNumber = "12345";

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", documentNumber, "nit", Collections.emptyList());

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        orderHandler.cancelOrder(orderId);

        // Assert
        verify(orderServicePort).cancelOrder(orderId, documentNumber);
    }
}
