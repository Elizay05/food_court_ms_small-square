package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.api.IOrderServicePort;
import com.example.food_court_ms_small_square.domain.exception.InvalidArgumentsException;
import com.example.food_court_ms_small_square.domain.exception.OrderInProgressException;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.OrderDish;
import com.example.food_court_ms_small_square.domain.spi.IOrderPersistencePort;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;

    @Override
    public Order saveOrder(Order order) {
        validateDishesBelongToRestaurant(order.getPlatos(), order.getNitRestaurante());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String documentNumber = userDetails.getDocumentNumber();

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
}
