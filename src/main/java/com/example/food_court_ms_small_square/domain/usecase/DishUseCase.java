package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExists;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.spi.IDishPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;

    @Override
    public void saveDish(Dish dish) {
        if (Boolean.TRUE.equals(dishPersistencePort.dishExists(dish.getNombre(), dish.getRestauranteNit())))
            throw new ElementAlreadyExists("El plato ya existe");
        dish.setActivo(true);
        dishPersistencePort.saveDish(dish);
    }
}
