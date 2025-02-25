package com.example.food_court_ms_small_square.application.handler;

import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.RestaurantResponseDto;
import org.springframework.data.domain.Page;

public interface IRestaurantHandler {

    void saveRestaurant(RestaurantRequestDto restaurantRequestDto);
    String validateNit();
    Page<RestaurantResponseDto> listRestaurants (int page, int size);
}
