package com.example.food_court_ms_small_square.domain.spi;

import com.example.food_court_ms_small_square.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRestaurantPersistencePort {
    Restaurant saveRestaurant(Restaurant restaurant);
    Boolean restaurantExists(String nit);
    String validateNit();
    Page<Restaurant> listRestaurants(Pageable pageable);
}
