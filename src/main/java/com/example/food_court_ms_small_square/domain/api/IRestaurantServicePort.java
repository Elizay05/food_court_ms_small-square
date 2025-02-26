package com.example.food_court_ms_small_square.domain.api;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.model.Restaurant;

public interface IRestaurantServicePort {
    void saveRestaurant(Restaurant restaurant);
    String validateNit();
    Page<Restaurant> listRestaurants(PageRequestDto pageRequestModel);
}
