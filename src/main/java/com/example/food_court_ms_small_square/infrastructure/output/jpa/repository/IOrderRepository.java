package com.example.food_court_ms_small_square.infrastructure.output.jpa.repository;

import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
    int countByEstadoAndIdCliente(@Param("estado") String estado, @Param("clienteId") String clienteId);
    Page<OrderEntity> findByNitRestauranteAndEstado(String nit, String estado, Pageable pageable);
    Page<OrderEntity> findByNitRestaurante(String nit, Pageable pageable);

}
