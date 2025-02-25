package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.exception.InvalidArgumentsException;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.spi.IDishPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;

    @Override
    public void saveDish(Dish dish) {
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

    @Override
    public void updateDishStatus(Long id, Boolean enabled) {
        dishPersistencePort.updateDishStatus(id, enabled);
    }

    @Override
    public Page<Dish> listDishes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return dishPersistencePort.listDishes(pageable);
    }
}
