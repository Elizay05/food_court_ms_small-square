package com.example.food_court_ms_small_square.domain.spi;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.Page;

import java.util.List;

public interface IOrderPersistencePort {
    Order saveOrder(Order order);
    boolean areDishesValidForRestaurant(List<Long> dishIds, String nitRestaurante);
    boolean hasActiveOrders(String clienteId);
    Page<Order> listOrdersByFilters(String nit, String estado, PageRequestDto pageRequestDto);
}
