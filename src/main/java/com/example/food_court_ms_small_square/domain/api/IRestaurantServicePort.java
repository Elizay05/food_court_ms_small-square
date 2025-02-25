package com.example.food_court_ms_small_square.domain.api;

import com.example.food_court_ms_small_square.domain.model.Restaurant;
import org.springframework.data.domain.Page;

public interface IRestaurantServicePort {
    void saveRestaurant(Restaurant restaurant);
    String validateNit();
    Page<Restaurant> listRestaurants(int page, int size);
}
