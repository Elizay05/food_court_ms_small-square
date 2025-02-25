package com.example.food_court_ms_small_square.infrastructure.output.jpa.repository;

import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDishRespository extends JpaRepository<DishEntity, Long> {
    boolean existsByNombreAndRestauranteNit(String nombre, String restauranteNit);
    Page<DishEntity> findByRestauranteNitAndActivo(String restauranteNit, Boolean activo, Pageable pageable);
    Page<DishEntity> findByRestauranteNitAndActivoAndCategoriaId(String restauranteNit, Boolean activo, Long categoriaId, Pageable pageable);
}
