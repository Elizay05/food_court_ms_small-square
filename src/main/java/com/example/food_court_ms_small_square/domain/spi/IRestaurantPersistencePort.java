package com.example.food_court_ms_small_square.domain.spi;

import com.example.food_court_ms_small_square.domain.model.Restaurant;

public interface IRestaurantPersistencePort {
    Restaurant saveRestaurant(Restaurant restaurant);

    Boolean restaurantExists(String nit);
}
