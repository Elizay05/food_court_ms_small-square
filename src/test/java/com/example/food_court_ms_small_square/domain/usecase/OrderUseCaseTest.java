package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.exception.*;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.OrderDish;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.spi.IMessagePersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IOrderPersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
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

    @Mock
    private IUserValidationPersistencePort userValidationPersistencePort;

    @Mock
    private IMessagePersistencePort messagePersistencePort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    @Test
    public void test_save_order_success() {
        // Arrange
        List<OrderDish> dishes = Arrays.asList(new OrderDish(1L, 2));
        Order order = new Order(null, null, "123456789", null, null, null, "123456", dishes);

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
        Order order = new Order(null, null, "123456789", null, null, null, "123456", dishes);

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
        Order order = new Order(null, null, "123456789", null, null, null, "123456", dishes);

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
        OrderUseCase orderUseCase = new OrderUseCase(orderPersistencePort, userValidationPersistencePort, messagePersistencePort);

        String status = "PENDING";
        String nit = "123456789";
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "id", true);

        List<Order> orders = Arrays.asList(new Order(1L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", "123456", new ArrayList<>()),
                new Order(2L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", "123456", new ArrayList<>()));
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
        OrderUseCase orderUseCase = new OrderUseCase(orderPersistencePort, userValidationPersistencePort, messagePersistencePort);

        String nit = "123456789";
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "id", true);

        List<Order> orders = Arrays.asList(new Order(1L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", "123456", new ArrayList<>()),
                new Order(2L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", "123456", new ArrayList<>()),
                new Order(3L, "111", "222", LocalDateTime.now(), "PENDING", "123456789", "123456", new ArrayList<>()));
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
                DomainConstants.ORDER_STATUS_PENDING, null, "123456", new ArrayList<>());

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
    public void test_assign_order_with_non_pending_status() {
        Order order = new Order(1L, "client1", "nit123", LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_READY, null, "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidOrderStatusException.class, () -> {
            orderUseCase.assignOrder(1L, "chef1", "nit123");
        });
    }

    @Test
    public void test_assign_order_different_restaurant_throws_exception() {
        Order mockOrder = new Order(1L, "client1", "nit123", LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_PENDING, null, "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(mockOrder));

        // Act & Assert
        assertThrows(OrderAssignmentNotAllowedException.class, () -> {
            orderUseCase.assignOrder(1L, "chef1", "differentNit");
        });
    }

    @Test
    public void test_mark_order_as_ready_success() {
        // Arrange
        Long orderId = 1L;
        String nitRestaurant = "123456789";
        String clientId = "C001";
        String phoneNumber = "1234567890";

        Order mockOrder = new Order(orderId, clientId, nitRestaurant, LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_IN_PROGRESS, "CHEF1", null, new ArrayList<>());

        when(orderPersistencePort.getOrderById(orderId)).thenReturn(Optional.of(mockOrder));
        when(userValidationPersistencePort.getPhoneNumberByDocumentNumber(clientId)).thenReturn(phoneNumber);
        when(orderPersistencePort.updateOrder(any(Order.class))).thenReturn(mockOrder);

        // Act
        Order result = orderUseCase.readyOrder(orderId, nitRestaurant);

        // Assert
        assertEquals(DomainConstants.ORDER_STATUS_READY, result.getEstado());
        assertNotNull(result.getPin());
        verify(messagePersistencePort).sendOrderReadyMessage(eq(phoneNumber), anyString());
        verify(orderPersistencePort).updateOrder(any(Order.class));
    }

    @Test
    public void test_ready_order_not_found() {
        // Arrange
        Long orderId = 999L;
        String nitRestaurant = "123456";

        when(orderPersistencePort.getOrderById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderUseCase.readyOrder(orderId, nitRestaurant);
        });

        verify(orderPersistencePort).getOrderById(orderId);
        verifyNoMoreInteractions(orderPersistencePort);
        verifyNoInteractions(messagePersistencePort);
        verifyNoInteractions(userValidationPersistencePort);
    }

    @Test
    public void test_ready_order_wrong_nit() {
        // Arrange
        Long orderId = 1L;
        String correctNitRestaurant = "123456";
        String wrongNitRestaurant = "654321";
        String clientId = "C001";
        String chefId = "CHEF1";

        Order order = new Order(orderId, clientId, correctNitRestaurant, LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_IN_PROGRESS, chefId, "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(OrderAssignmentNotAllowedException.class, () -> {
            orderUseCase.readyOrder(orderId, wrongNitRestaurant);
        });

        verify(orderPersistencePort, never()).updateOrder(any(Order.class));
        verify(messagePersistencePort, never()).sendOrderReadyMessage(anyString(), anyString());
    }

    @Test
    public void test_ready_order_already_ready_exception() {
        // Arrange
        Long orderId = 1L;
        String nitRestaurant = "123456";
        String clientId = "C001";
        String chefId = "CHEF1";

        Order order = new Order(orderId, clientId, nitRestaurant, LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_READY, chefId, "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(OrderAlreadyReadyException.class, () -> {
            orderUseCase.readyOrder(orderId, nitRestaurant);
        });

        verify(orderPersistencePort, never()).updateOrder(any(Order.class));
        verify(messagePersistencePort, never()).sendOrderReadyMessage(anyString(), anyString());
    }

    @Test
    public void test_ready_order_invalid_status_exception() {
        // Arrange
        Long orderId = 1L;
        String nitRestaurant = "123456";
        String clientId = "C001";
        String chefId = "CHEF1";

        Order order = new Order(orderId, clientId, nitRestaurant, LocalDateTime.now(),
                DomainConstants.ORDER_STATUS_PENDING, chefId, "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidOrderStatusException.class, () -> {
            orderUseCase.readyOrder(orderId, nitRestaurant);
        });

        verify(orderPersistencePort, never()).updateOrder(any(Order.class));
        verify(messagePersistencePort, never()).sendOrderReadyMessage(anyString(), anyString());
    }

    @Test
    public void test_delivered_order_success() {
        Order order = new Order(1L, "client1", "nit123", LocalDateTime.now(), DomainConstants.ORDER_STATUS_READY, "chef1", "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(order));
        when(orderPersistencePort.updateOrder(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderUseCase.deliveredOrder(1L, "123456", "nit123");

        // Assert
        assertEquals(DomainConstants.ORDER_STATUS_DELIVERED, result.getEstado());
        verify(orderPersistencePort).updateOrder(order);
    }

    @Test
    public void test_delivered_order_not_found() {
        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderUseCase.deliveredOrder(1L, "123456", "nit123");
        });
    }

    @Test
    public void test_delivered_order_nit_mismatch() {
        Order order = new Order(1L, "client1", "nit123", LocalDateTime.now(), DomainConstants.ORDER_STATUS_READY, "chef1", "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(OrderAssignmentNotAllowedException.class, () -> {
            orderUseCase.deliveredOrder(1L, "123456", "wrongNit");
        });
    }

    @Test
    public void test_delivered_order_status_not_ready() {
        Order order = new Order(1L, "client1", "nit123", LocalDateTime.now(), DomainConstants.ORDER_STATUS_PENDING, "chef1", "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidOrderStatusException.class, () -> {
            orderUseCase.deliveredOrder(1L, "123456", "nit123");
        });
    }

    @Test
    public void test_delivered_order_pin_mismatch() {
        Order order = new Order(1L, "client1", "nit123", LocalDateTime.now(), DomainConstants.ORDER_STATUS_READY, "chef1", "123456", new ArrayList<>());

        when(orderPersistencePort.getOrderById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidPinException.class, () -> {
            orderUseCase.deliveredOrder(1L, "654321", "nit123");
        });
    }
}
