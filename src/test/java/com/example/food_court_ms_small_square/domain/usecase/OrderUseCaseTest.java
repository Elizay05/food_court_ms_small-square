package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.exception.InvalidArgumentsException;
import com.example.food_court_ms_small_square.domain.exception.OrderInProgressException;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.OrderDish;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        CustomUserDetails userDetails = new CustomUserDetails("user", "12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderPersistencePort.areDishesValidForRestaurant(anyList(), anyString())).thenReturn(true);
        when(orderPersistencePort.hasActiveOrders(anyString())).thenReturn(false);
        when(orderPersistencePort.saveOrder(any(Order.class))).thenReturn(order);

        // Act
        Order savedOrder = orderUseCase.saveOrder(order);

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
        CustomUserDetails userDetails = new CustomUserDetails("user", "12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderPersistencePort.areDishesValidForRestaurant(anyList(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidArgumentsException.class, () -> orderUseCase.saveOrder(order));
        verify(orderPersistencePort, never()).saveOrder(any(Order.class));
    }

    @Test
    public void test_save_order_throws_order_in_progress_exception() {
        // Arrange
        List<OrderDish> dishes = Arrays.asList(new OrderDish(1L, 2));
        Order order = new Order(null, null, "123456789", null, null, null, dishes);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(orderPersistencePort.areDishesValidForRestaurant(anyList(), anyString())).thenReturn(true);
        when(orderPersistencePort.hasActiveOrders(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(OrderInProgressException.class, () -> orderUseCase.saveOrder(order));
    }
}
