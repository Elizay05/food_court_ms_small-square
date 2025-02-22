package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExistsException;
import com.example.food_court_ms_small_square.domain.exception.InvalidArgumentsException;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.spi.IDishPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;

    @Override
    public void saveDish(Dish dish) {
        if (Boolean.TRUE.equals(dishPersistencePort.dishExists(dish.getNombre(), dish.getRestauranteNit())))
            throw new ElementAlreadyExistsException("El plato ya existe");
        dish.setActivo(true);
        dishPersistencePort.saveDish(dish);
    }

    @Override
    public void updateDish(Long id, Float price, String description) {
        if (description == null && price == null) {
            throw new InvalidArgumentsException("Debe proporcionar al menos un campo para actualizar.");
        }
        dishPersistencePort.updateDish(id, price, description);
    }
}
