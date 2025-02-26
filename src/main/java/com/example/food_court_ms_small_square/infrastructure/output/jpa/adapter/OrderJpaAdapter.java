package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.spi.IOrderPersistencePort;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderDishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IOrderDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IDishRespository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IOrderDishRepository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;
    private final IOrderDishEntityMapper orderDishEntityMapper;
    private final IOrderDishRepository orderDishRepository;
    private final IDishRespository dishRespository;

    @Override
    public Order saveOrder(Order order) {
        OrderEntity orderEntity = orderRepository.save(orderEntityMapper.toEntity(order));

        List<OrderDishEntity> orderDishEntities = order.getPlatos().stream()
                .map(plato -> orderDishEntityMapper.toEntity(plato, orderEntity))
                .collect(Collectors.toList());

        orderDishRepository.saveAll(orderDishEntities);

        List<OrderDishEntity> savedPlatos = orderDishRepository.findByOrden(orderEntity);
        Order mappedOrder = orderEntityMapper.toDomain(orderEntity);
        mappedOrder.setPlatos(orderDishEntityMapper.toDomainList(savedPlatos));

        return mappedOrder;
    }

    @Override
    public boolean areDishesValidForRestaurant(List<Long> dishIds, String nitRestaurante) {
        int validDishCount = dishRespository.countByIdInAndRestauranteNit(dishIds, nitRestaurante);
        return validDishCount == dishIds.size();
    }

    @Override
    public boolean hasActiveOrders(String clienteId) {
        return orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_PENDING, clienteId)
                + orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, (clienteId)) > 0;
    }
}
