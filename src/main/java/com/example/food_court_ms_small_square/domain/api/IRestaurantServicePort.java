package com.example.food_court_ms_small_square.domain.api;

import com.example.food_court_ms_small_square.domain.model.Restaurant;

public interface IRestaurantServicePort {

    void saveRestaurant(Restaurant restaurant);
}
