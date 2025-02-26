package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.OrderDish;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderDishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderDishPk;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IOrderDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IDishRespository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IOrderDishRepository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IOrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderJpaAdapterTest {

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private IOrderEntityMapper orderEntityMapper;

    @Mock
    private IOrderDishEntityMapper orderDishEntityMapper;

    @Mock
    private IOrderDishRepository orderDishRepository;

    @Mock
    private IDishRespository dishRepository;

    @InjectMocks
    private OrderJpaAdapter orderJpaAdapter;

    @Test
    public void test_save_order_with_multiple_dishes_success() {
        // Arrange
        Order order = new Order(1L, "client1", "rest1", LocalDateTime.now(), "PENDING", null,
                Arrays.asList(new OrderDish(1L, 2), new OrderDish(2L, 1)));

        OrderEntity orderEntity = new OrderEntity(1L, "client1", "rest1", LocalDateTime.now(), "PENDING", null);
        List<OrderDishEntity> orderDishEntities = Arrays.asList(
                new OrderDishEntity(new OrderDishPk(1L, 1L), orderEntity, null, 2),
                new OrderDishEntity(new OrderDishPk(1L, 2L), orderEntity, null, 1)
        );

        when(orderEntityMapper.toEntity(order)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderDishEntityMapper.toEntity(any(), eq(orderEntity))).thenReturn(orderDishEntities.get(0));
        when(orderDishRepository.saveAll(any())).thenReturn(orderDishEntities);
        when(orderDishRepository.findByOrden(orderEntity)).thenReturn(orderDishEntities);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);
        when(orderDishEntityMapper.toDomainList(orderDishEntities)).thenReturn(order.getPlatos());

        // Act
        Order savedOrder = orderJpaAdapter.saveOrder(order);

        // Assert
        assertNotNull(savedOrder);
        assertEquals(order.getId(), savedOrder.getId());
        assertEquals(2, savedOrder.getPlatos().size());
        verify(orderRepository).save(any());
        verify(orderDishRepository).saveAll(any());
    }

    @Test
    public void test_all_dishes_belong_to_restaurant() {
        // Arrange
        List<Long> dishIds = Arrays.asList(1L, 2L, 3L);
        String restaurantNit = "123456789";
        when(dishRepository.countByIdInAndRestauranteNit(dishIds, restaurantNit)).thenReturn(3);

        // Act
        boolean result = orderJpaAdapter.areDishesValidForRestaurant(dishIds, restaurantNit);

        // Assert
        assertTrue(result);
        verify(dishRepository).countByIdInAndRestauranteNit(dishIds, restaurantNit);
    }

    @Test
    public void test_no_dishes_belong_to_restaurant() {
        // Arrange
        List<Long> dishIds = Arrays.asList(1L, 2L, 3L);
        String restaurantNit = "123456789";
        when(dishRepository.countByIdInAndRestauranteNit(dishIds, restaurantNit)).thenReturn(0);

        // Act
        boolean result = orderJpaAdapter.areDishesValidForRestaurant(dishIds, restaurantNit);

        // Assert
        assertFalse(result);
        verify(dishRepository).countByIdInAndRestauranteNit(dishIds, restaurantNit);
    }

    @Test
    public void test_empty_dish_list() {
        // Arrange
        List<Long> emptyDishIds = Collections.emptyList();
        String restaurantNit = "123456789";
        when(dishRepository.countByIdInAndRestauranteNit(emptyDishIds, restaurantNit)).thenReturn(0);

        // Act
        boolean result = orderJpaAdapter.areDishesValidForRestaurant(emptyDishIds, restaurantNit);

        // Assert
        assertTrue(result);
        verify(dishRepository).countByIdInAndRestauranteNit(emptyDishIds, restaurantNit);
    }

    @Test
    public void test_no_active_orders_returns_false() {
        // Arrange
        String clientId = "123";

        when(orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clientId))
                .thenReturn(0);
        when(orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clientId))
                .thenReturn(0);

        // Act
        boolean result = orderJpaAdapter.hasActiveOrders(clientId);

        // Assert
        assertFalse(result);
        verify(orderRepository).countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clientId);
        verify(orderRepository).countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clientId);
    }

    @Test
    public void test_client_with_pending_orders_returns_true() {
        // Arrange
        String clientId = "123";

        when(orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clientId))
                .thenReturn(1);
        when(orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clientId))
                .thenReturn(0);

        // Act
        boolean result = orderJpaAdapter.hasActiveOrders(clientId);

        // Assert
        assertTrue(result);
        verify(orderRepository).countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clientId);
        verify(orderRepository).countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clientId);
    }

    @Test
    public void test_client_with_in_progress_orders_returns_true() {
        // Arrange
        String clientId = "123";

        when(orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clientId))
                .thenReturn(0);
        when(orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clientId))
                .thenReturn(1);

        // Act
        boolean result = orderJpaAdapter.hasActiveOrders(clientId);

        // Assert
        assertTrue(result);
        verify(orderRepository).countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clientId);
        verify(orderRepository).countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clientId);
    }

    @Test
    public void test_null_client_id_returns_false() {
        // Arrange
        String clientId = null;

        when(orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clientId))
                .thenReturn(0);
        when(orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clientId))
                .thenReturn(0);

        // Act
        boolean result = orderJpaAdapter.hasActiveOrders(clientId);

        // Assert
        assertFalse(result);
        verify(orderRepository).countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clientId);
        verify(orderRepository).countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clientId);
    }
}
