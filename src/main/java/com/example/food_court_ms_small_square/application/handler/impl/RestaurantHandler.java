package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.RestaurantResponseDto;
import com.example.food_court_ms_small_square.application.handler.IRestaurantHandler;
import com.example.food_court_ms_small_square.application.mapper.IRestaurantRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IRestaurantServicePort;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;

    @Override
    public void saveRestaurant(RestaurantRequestDto restaurantRequestDto) {
        Restaurant restaurant = restaurantRequestMapper.toRestaurant(restaurantRequestDto);
        restaurantServicePort.saveRestaurant(restaurant);
    }

    @Override
    public String validateNit(){
        return restaurantServicePort.validateNit();
    }

    @Override
    public Page<RestaurantResponseDto> listRestaurants(int page, int size) {
        return restaurantServicePort.listRestaurants(page, size)
                .map(restaurantRequestMapper::toResponseDto);
    }
}
