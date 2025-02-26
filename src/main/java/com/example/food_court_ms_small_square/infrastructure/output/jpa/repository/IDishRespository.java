package com.example.food_court_ms_small_square.infrastructure.output.jpa.repository;

import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDishRespository extends JpaRepository<DishEntity, Long> {
    boolean existsByNombreAndRestauranteNit(String nombre, String restauranteNit);
    Page<DishEntity> findByRestauranteNitAndActivo(String restauranteNit, Boolean activo, Pageable pageable);
    Page<DishEntity> findByRestauranteNitAndActivoAndCategoriaId(String restauranteNit, Boolean activo, Long categoriaId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM DishEntity d WHERE d.id IN :dishIds AND d.restauranteNit = :restauranteNit")
    int countByIdInAndRestauranteNit(@Param("dishIds") List<Long> dishIds, @Param("restauranteNit") String restauranteNit);
}
