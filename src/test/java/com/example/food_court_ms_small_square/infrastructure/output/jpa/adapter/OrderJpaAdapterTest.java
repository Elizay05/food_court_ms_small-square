package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.OrderDish;
import com.example.food_court_ms_small_square.domain.model.Page;
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
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Test
    public void test_list_orders_with_valid_filters() {
        // Arrange
        String nit = "123456";
        String estado = "PENDING";
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "fecha", true);

        OrderEntity orderEntity = new OrderEntity(1L, "client1", nit, LocalDateTime.now(), estado, "chef1");
        List<OrderEntity> orderEntities = Collections.singletonList(orderEntity);

        OrderDishEntity orderDishEntity = new OrderDishEntity();
        List<OrderDishEntity> orderDishEntities = Collections.singletonList(orderDishEntity);

        Order order = new Order(1L, "client1", nit, LocalDateTime.now(), estado, "chef1", new ArrayList<>());
        OrderDish orderDish = new OrderDish(1L, 2);
        List<OrderDish> orderDishes = Collections.singletonList(orderDish);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("fecha").ascending());
        org.springframework.data.domain.Page<OrderEntity> page = new PageImpl<>(orderEntities, pageable, 1);

        when(orderRepository.findByNitRestauranteAndEstado(nit, estado, pageable)).thenReturn(page);
        when(orderDishRepository.findByOrden(orderEntity)).thenReturn(orderDishEntities);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);
        when(orderDishEntityMapper.toDomainList(orderDishEntities)).thenReturn(orderDishes);

        // Act
        Page<Order> result = orderJpaAdapter.listOrdersByFilters(nit, estado, pageRequestDto);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(order, result.getContent().get(0));
        assertEquals(orderDishes, result.getContent().get(0).getPlatos());
    }

    @Test
    public void test_list_orders_when_estado_is_null() {
        // Arrange
        String nit = "123456";
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "fecha", true);

        OrderEntity orderEntity = new OrderEntity(1L, "client1", nit, LocalDateTime.now(), null, "chef1");
        List<OrderEntity> orderEntities = Collections.singletonList(orderEntity);

        OrderDishEntity orderDishEntity = new OrderDishEntity();
        List<OrderDishEntity> orderDishEntities = Collections.singletonList(orderDishEntity);

        Order order = new Order(1L, "client1", nit, LocalDateTime.now(), null, "chef1", new ArrayList<>());
        OrderDish orderDish = new OrderDish(1L, 2);
        List<OrderDish> orderDishes = Collections.singletonList(orderDish);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("fecha").ascending());
        org.springframework.data.domain.Page<OrderEntity> page = new PageImpl<>(orderEntities, pageable, 1);

        when(orderRepository.findByNitRestaurante(nit, pageable)).thenReturn(page);
        when(orderDishRepository.findByOrden(orderEntity)).thenReturn(orderDishEntities);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);
        when(orderDishEntityMapper.toDomainList(orderDishEntities)).thenReturn(orderDishes);

        // Act
        Page<Order> result = orderJpaAdapter.listOrdersByFilters(nit, null, pageRequestDto);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(order, result.getContent().get(0));
        assertEquals(orderDishes, result.getContent().get(0).getPlatos());
    }

    @Test
    public void test_list_orders_sorted_descending() {
        // Arrange
        String nit = "123456";
        String estado = "PENDING";
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "fecha", false);

        OrderEntity orderEntity = new OrderEntity(1L, "client1", nit, LocalDateTime.now(), estado, "chef1");
        List<OrderEntity> orderEntities = Collections.singletonList(orderEntity);

        OrderDishEntity orderDishEntity = new OrderDishEntity();
        List<OrderDishEntity> orderDishEntities = Collections.singletonList(orderDishEntity);

        Order order = new Order(1L, "client1", nit, LocalDateTime.now(), estado, "chef1", new ArrayList<>());
        OrderDish orderDish = new OrderDish(1L, 2);
        List<OrderDish> orderDishes = Collections.singletonList(orderDish);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("fecha").descending());
        org.springframework.data.domain.Page<OrderEntity> page = new PageImpl<>(orderEntities, pageable, 1);

        when(orderRepository.findByNitRestauranteAndEstado(nit, estado, pageable)).thenReturn(page);
        when(orderDishRepository.findByOrden(orderEntity)).thenReturn(orderDishEntities);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);
        when(orderDishEntityMapper.toDomainList(orderDishEntities)).thenReturn(orderDishes);

        // Act
        Page<Order> result = orderJpaAdapter.listOrdersByFilters(nit, estado, pageRequestDto);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(order, result.getContent().get(0));
        assertEquals(orderDishes, result.getContent().get(0).getPlatos());
    }

    @Test
    public void test_sorts_results_ascending_when_is_ascending_true() {
        // Arrange
        String nit = "123456";
        String estado = "PENDING";
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "fecha", true);

        OrderEntity orderEntity1 = new OrderEntity(1L, "client1", nit, LocalDateTime.now().minusDays(1), estado, "chef1");
        OrderEntity orderEntity2 = new OrderEntity(2L, "client2", nit, LocalDateTime.now(), estado, "chef2");
        List<OrderEntity> orderEntities = Arrays.asList(orderEntity1, orderEntity2);

        OrderDishEntity orderDishEntity = new OrderDishEntity();
        List<OrderDishEntity> orderDishEntities = Collections.singletonList(orderDishEntity);

        Order order1 = new Order(1L, "client1", nit, LocalDateTime.now().minusDays(1), estado, "chef1", new ArrayList<>());
        Order order2 = new Order(2L, "client2", nit, LocalDateTime.now(), estado, "chef2", new ArrayList<>());
        OrderDish orderDish = new OrderDish(1L, 2);
        List<OrderDish> orderDishes = Collections.singletonList(orderDish);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("fecha").ascending());
        org.springframework.data.domain.Page<OrderEntity> page = new PageImpl<>(orderEntities, pageable, 2);

        when(orderRepository.findByNitRestauranteAndEstado(nit, estado, pageable)).thenReturn(page);
        when(orderDishRepository.findByOrden(orderEntity1)).thenReturn(orderDishEntities);
        when(orderDishRepository.findByOrden(orderEntity2)).thenReturn(orderDishEntities);
        when(orderEntityMapper.toDomain(orderEntity1)).thenReturn(order1);
        when(orderEntityMapper.toDomain(orderEntity2)).thenReturn(order2);
        when(orderDishEntityMapper.toDomainList(orderDishEntities)).thenReturn(orderDishes);

        // Act
        Page<Order> result = orderJpaAdapter.listOrdersByFilters(nit, estado, pageRequestDto);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertEquals(order1, result.getContent().get(0));
        assertEquals(order2, result.getContent().get(1));
    }

    @Test
    public void test_list_orders_with_no_matches() {
        // Arrange
        String nit = "123456";
        String estado = "COMPLETED";
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "fecha", true);

        List<OrderEntity> emptyList = Collections.emptyList();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("fecha").ascending());
        org.springframework.data.domain.Page<OrderEntity> emptyPage = new PageImpl<>(emptyList, pageable, 0);

        when(orderRepository.findByNitRestauranteAndEstado(nit, estado, pageable)).thenReturn(emptyPage);

        // Act
        Page<Order> result = orderJpaAdapter.listOrdersByFilters(nit, estado, pageRequestDto);

        // Assert
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getContent().isEmpty());
    }
}
