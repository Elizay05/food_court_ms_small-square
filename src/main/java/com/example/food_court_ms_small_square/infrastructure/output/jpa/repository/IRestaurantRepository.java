package com.example.food_court_ms_small_square.infrastructure.output.jpa.repository;

import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, String> {
    Optional<RestaurantEntity> findByCedulaPropietario(String ownerDni);
}
