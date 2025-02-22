package com.example.food_court_ms_small_square.infrastructure.output.jpa.repository;

import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDishRespository extends JpaRepository<DishEntity, Long> {
    boolean existsByNombreAndRestauranteNit(String nombre, String restauranteNit);
}
