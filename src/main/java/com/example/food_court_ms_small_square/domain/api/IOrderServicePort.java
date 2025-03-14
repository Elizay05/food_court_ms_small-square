package com.example.food_court_ms_small_square.domain.api;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.Page;


public interface IOrderServicePort {
    Order saveOrder(Order order, String documentNumber);
    Page<Order> listOrdersByFilters(String estado, PageRequestDto pageRequestDto, String nit);
    Order assignOrder(Long orderId, String documentNumber, String nitRestaurant);
    Order readyOrder(Long orderId, String nitRestaurant);
    Order deliveredOrder(Long orderId, String pin, String nitRestaurant);
    void cancelOrder(Long orderId, String documentNumber);
}
