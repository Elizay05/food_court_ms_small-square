package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.RestaurantResponseDto;
import com.example.food_court_ms_small_square.application.handler.IRestaurantHandler;
import com.example.food_court_ms_small_square.application.mapper.IRestaurantRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IRestaurantServicePort;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public PageResponseDto<RestaurantResponseDto> listRestaurants(int page, int size) {
        PageRequestDto pageRequestDto = new PageRequestDto(page, size, "nombre", true);

        Page<Restaurant> restaurantPage = restaurantServicePort.listRestaurants(pageRequestDto);

        List<RestaurantResponseDto> restaurantDtos = restaurantPage.getContent().stream()
                .map(restaurantRequestMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(restaurantDtos, restaurantPage.getTotalPages(), restaurantPage.getTotalElements());
    }
}
