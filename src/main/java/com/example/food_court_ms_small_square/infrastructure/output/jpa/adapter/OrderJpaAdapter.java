package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.exception.OrderNotFoundException;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.Page;
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
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
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
                + orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_IN_PROGRESS, clienteId)
                + orderRepository.countByEstadoAndIdCliente(DomainConstants.ORDER_STATUS_READY, clienteId) > 0;
    }

    @Override
    public Page<Order> listOrdersByFilters(String nit, String estado, PageRequestDto pageRequestDto) {
        Sort sort = pageRequestDto.isAscending()
                ? Sort.by(pageRequestDto.getSortBy()).ascending()
                : Sort.by(pageRequestDto.getSortBy()).descending();

        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);

        org.springframework.data.domain.Page<OrderEntity> orderEntities = (estado == null)
                ? orderRepository.findByNitRestaurante(nit, pageable)
                : orderRepository.findByNitRestauranteAndEstado(nit, estado, pageable);

        List<Order> ordersWithDishes = orderEntities.getContent().stream()
                .map(orderEntity -> {
                    List<OrderDishEntity> orderDishEntities = orderDishRepository.findByOrden(orderEntity);
                    Order mappedOrder = orderEntityMapper.toDomain(orderEntity);
                    mappedOrder.setPlatos(orderDishEntityMapper.toDomainList(orderDishEntities));
                    return mappedOrder;
                })
                .collect(Collectors.toList());

        return new Page<>(ordersWithDishes, orderEntities.getTotalPages(), orderEntities.getTotalElements());
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderEntityMapper::toDomain);
    }

    @Override
    public Order updateOrder(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(order);
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        List<OrderDishEntity> orderDishEntities = orderDishRepository.findByOrden(savedOrderEntity);

        Order mappedOrder = orderEntityMapper.toDomain(savedOrderEntity);
        mappedOrder.setPlatos(orderDishEntityMapper.toDomainList(orderDishEntities));

        return mappedOrder;
    }

    @Override
    public void deleteOrder(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada."));

        orderDishRepository.deleteByOrden(orderEntity);
        orderRepository.delete(orderEntity);
    }
}
