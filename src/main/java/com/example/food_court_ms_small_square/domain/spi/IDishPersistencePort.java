package com.example.food_court_ms_small_square.domain.spi;

import com.example.food_court_ms_small_square.domain.model.Dish;

public interface IDishPersistencePort {
    void saveDish(Dish dish);

    Boolean dishExists(String name, String nit);
}
