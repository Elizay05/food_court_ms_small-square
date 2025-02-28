package com.example.food_court_ms_small_square.infrastructure.output.jpa.repository;

import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderDishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderDishPk;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderDishRepository extends JpaRepository<OrderDishEntity, OrderDishPk> {
    List<OrderDishEntity> findByOrden(OrderEntity order);
    void deleteByOrden(OrderEntity order);
}
