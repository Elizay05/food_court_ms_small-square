package com.example.food_court_ms_small_square.domain.spi;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.model.Restaurant;

public interface IRestaurantPersistencePort {
    Restaurant saveRestaurant(Restaurant restaurant);
    Boolean restaurantExists(String nit);
    String validateNit();
    Page<Restaurant> listRestaurants(PageRequestDto pageRequestModel);
}
