package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.spi.IDishPersistencePort;
import com.example.food_court_ms_small_square.infrastructure.exception.NoSuchElementException;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IDishRespository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishEntityMapper dishEntityMapper;
    private final IDishRespository dishRepository;

    @Override
    public void saveDish(Dish dish) {
        DishEntity dishEntity = dishEntityMapper.toEntity(dish);
        dishRepository.save(dishEntity);
    }

    @Override
    public Boolean dishExists(String name, String nit) {
        return dishRepository.existsByNombreAndRestauranteNit(name, nit);
    }

    @Override
    public void updateDish(Long id, Float precio, String descripcion) {
        DishEntity dishEntity = dishRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El plato no existe."));

        if (descripcion != null) {
            dishEntity.setDescripcion(descripcion);
        }
        if (precio != null) {
            dishEntity.setPrecio(precio);
        }

        dishRepository.save(dishEntity);
    }
}
