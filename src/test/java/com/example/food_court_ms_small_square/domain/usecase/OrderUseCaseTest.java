package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.exception.*;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.OrderDish;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.spi.IOrderPersistencePort;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    @Test
    public void test_save_order_success() {
        // Arrange
        List<OrderDish> dishes = Arrays.asList(new OrderDish(1L, 2));
        Order order = new Order(null, null, "123456789", null, null, null, dishes);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "12345", "12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderPersistencePort.areDishesValidForRestaurant(anyList(), anyString())).thenReturn(true);
        when(orderPersistencePort.hasActiveOrders(anyString())).thenReturn(false);
        when(orderPersistencePort.saveOrder(any(Order.class))).thenReturn(order);

        // Act
        Order savedOrder = orderUseCase.saveOrder(order, "12345");

        // Assert
        assertNotNull(savedOrder);
        assertEquals(DomainConstants.ORDER_STATUS_PENDING, savedOrder.getEstado());
        assertEquals("12345", savedOrder.getIdCliente());
        verify(orderPersistencePort).saveOrder(order);
    }

    @Test
    public void test_save_order_invalid_dishes() {
        // Arrange
        List<OrderDish> dishes = Arrays.asList(new OrderDish(1L, 2));
        Order order = new Order(null, null, "123456789", null, null, null, dishes);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "12345","12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderPersistencePort.areDishesValidForRestaurant(anyList(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidArgumentsException.class, () -> orderUseCase.saveOrder(order, "12345"));
        verify(orderPersistencePort, never()).saveOrder(any(Order.class));
    }

    @Test
    public void test_save_order_throws_order_in_progress_exception() {
        // Arrange
        List<OrderDish> dishes = Arrays.asList(new OrderDish(1L, 2));
        Order order = new Order(null, null, "123456789", null, null, null, dishes);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "12345", "12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderPersistencePort.areDishesValidForRestaurant(anyList(), anyString())).thenReturn(true);
        when(orderPersistencePort.hasActiveOrders(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(OrderInProgressException.class, () -> orderUseCase.saveOrder(order, "12345"));
    }

    @Test
    public void test_list_orders_with_status_filter() {
        // Arrange
        IOrderPersistencePort orderPersistencePort = mock(IOrderPersistencePort.class);
        OrderUseCase orderUseCase = new OrderUseCase(orderPersistencePort);

        String status = "PENDING";
        String nit = "123456789";
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "id", true);

        List<Order> orders = Arrays.asList(new Order(1L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", new ArrayList<>()),
                new Order(2L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", new ArrayList<>()));
        Page<Order> expectedPage = new Page<>(orders, 1, 2);

        when(orderPersistencePort.listOrdersByFilters(nit, status, pageRequest))
                .thenReturn(expectedPage);

        // Act
        Page<Order> result = orderUseCase.listOrdersByFilters(status, pageRequest, nit);

        // Assert
        assertEquals(expectedPage.getContent(), result.getContent());
        assertEquals(expectedPage.getTotalPages(), result.getTotalPages());
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        verify(orderPersistencePort).listOrdersByFilters(nit, status, pageRequest);
    }

    @Test
    public void test_list_orders_with_null_status() {
        // Arrange
        IOrderPersistencePort orderPersistencePort = mock(IOrderPersistencePort.class);
        OrderUseCase orderUseCase = new OrderUseCase(orderPersistencePort);

        String nit = "123456789";
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "id", true);

        List<Order> orders = Arrays.asList(new Order(1L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", new ArrayList<>()),
                new Order(2L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", new ArrayList<>()),
                new Order(3L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", new ArrayList<>()));
        Page<Order> expectedPage = new Page<>(orders, 1, 3);

        when(orderPersistencePort.listOrdersByFilters(nit, null, pageRequest))
                .thenReturn(expectedPage);

        // Act
        Page<Order> result = orderUseCase.listOrdersByFilters(null, pageRequest, nit);

        // Assert
        assertEquals(expectedPage.getContent(), result.getContent());
        assertEquals(expectedPage.getTotalPages(), result.getTotalPages());
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        verify(orderPersistencePort).listOrdersByFilters(nit, null, pageRequest);
    }

    @Test
    public void test_assign_order_success() {
        Order mockOrder = new Order(1L, "client1", "nit123", LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_PENDING, null, new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderPersistencePort.updateOrder(any(Order.class))).thenReturn(mockOrder);

        // Act
        Order result = orderUseCase.assignOrder(1L, "chef1", "nit123");

        // Assert
        assertEquals("chef1", result.getIdChef());
        assertEquals(DomainConstants.ORDER_STATUS_IN_PROGRESS, result.getEstado());
        verify(orderPersistencePort).updateOrder(any(Order.class));
    }

    @Test
    public void test_assign_nonexistent_order_throws_exception() {
        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderUseCase.assignOrder(1L, "chef1", "nit123");
        });

        verify(orderPersistencePort).getOrderById(1L);
        verify(orderPersistencePort, never()).updateOrder(any(Order.class));
    }

    @Test
    public void test_assign_order_already_assigned_throws_exception() {
        Order mockOrder = new Order(1L, "client1", "nit123", LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_PENDING, "chef1", new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(mockOrder));

        // Act & Assert
        assertThrows(OrderAlreadyAssignedException.class, () -> {
            orderUseCase.assignOrder(1L, "chef2", "nit123");
        });
    }

    @Test
    public void test_assign_order_different_restaurant_throws_exception() {
        Order mockOrder = new Order(1L, "client1", "nit123", LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_PENDING, null, new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(mockOrder));

        // Act & Assert
        assertThrows(OrderAssignmentNotAllowedException.class, () -> {
            orderUseCase.assignOrder(1L, "chef1", "differentNit");
        });
    }
}
