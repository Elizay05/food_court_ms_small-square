package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.api.IOrderServicePort;
import com.example.food_court_ms_small_square.domain.exception.*;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.OrderDish;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.spi.IMessagePersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IOrderPersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IUserValidationPersistencePort userValidationPersistencePort;
    private final IMessagePersistencePort messagePersistencePort;

    @Override
    public Order saveOrder(Order order, String documentNumber) {
        validateDishesBelongToRestaurant(order.getPlatos(), order.getNitRestaurante());

        order.setIdCliente(documentNumber);

        if (orderPersistencePort.hasActiveOrders(order.getIdCliente())) {
            throw new OrderInProgressException("El cliente ya tiene un pedido en proceso.");
        }

        order.setEstado(DomainConstants.ORDER_STATUS_PENDING);
        order.setFecha(LocalDateTime.now());

        return orderPersistencePort.saveOrder(order);
    }

    private void validateDishesBelongToRestaurant(List<OrderDish> dishes, String nitRestaurante) {
        List<Long> dishIds = dishes.stream()
                .map(OrderDish::getIdPlato)
                .collect(Collectors.toList());

        boolean areValid = orderPersistencePort.areDishesValidForRestaurant(dishIds, nitRestaurante);

        if (!areValid) {
            throw new InvalidArgumentsException("Algunos platos no pertenecen al restaurante indicado.");
        }
    }

    @Override
    public Page<Order> listOrdersByFilters(String estado, PageRequestDto pageRequestDto, String nit) {
        Page<Order> orderPage = orderPersistencePort.listOrdersByFilters(nit, estado, pageRequestDto);
        return new Page<>(
                orderPage.getContent(),
                orderPage.getTotalPages(),
                orderPage.getTotalElements()
        );
    }

    @Override
    public Order assignOrder(Long orderId, String employeeDocument, String nitRestaurant) {
        Order order = orderPersistencePort.getOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada."));

        if (order.getIdChef() != null) {
            throw new OrderAlreadyAssignedException("La orden ya esta asignada a un chef.");
        }

        if (!Objects.equals(order.getNitRestaurante(), nitRestaurant)) {
            throw new OrderAssignmentNotAllowedException("No tienes permisos para asignar esta orden.");
        }

        order.setIdChef(employeeDocument);
        order.setEstado(DomainConstants.ORDER_STATUS_IN_PROGRESS);

        return orderPersistencePort.updateOrder(order);
    }

    @Override
    public Order readyOrder(Long orderId, String nitRestaurant) {
        Order order = orderPersistencePort.getOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada."));

        if (order.getIdChef() == null) {
            throw new OrderNotAssignedException("La orden no esta asignada a un chef.");
        }

        if (!Objects.equals(order.getNitRestaurante(), nitRestaurant)) {
            throw new OrderAssignmentNotAllowedException("No tienes permisos para marcar esta orden como lista.");
        }

        if (order.getEstado().equals(DomainConstants.ORDER_STATUS_READY)) {
            throw new OrderAlreadyReadyException("La orden ya esta lista.");
        }

        order.setEstado(DomainConstants.ORDER_STATUS_READY);

        String phoneNumber = userValidationPersistencePort.getPhoneNumberByDocumentNumber(order.getIdCliente());

        String message = String.format(DomainConstants.MESSAGE_READY_ORDER, order.getId());
        messagePersistencePort.sendOrderReadyMessage(phoneNumber, message);
        return orderPersistencePort.updateOrder(order);
    }
}
