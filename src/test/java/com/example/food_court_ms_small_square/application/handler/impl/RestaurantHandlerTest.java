package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.RestaurantResponseDto;
import com.example.food_court_ms_small_square.application.mapper.IRestaurantRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IRestaurantServicePort;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RestaurantHandlerTest {

    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Mock
    private IRestaurantRequestMapper restaurantRequestMapper;

    @InjectMocks
    private RestaurantHandler restaurantHandler;

    private RestaurantRequestDto restaurantRequestDto;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurantRequestDto = new RestaurantRequestDto("123456789", "987654321", "Mi Restaurante", "Calle 123","+573001234567","http://logo.com/logo.png");

        restaurant = new Restaurant(
                "123456789",
                "987654321",
                "Mi Restaurante",
                "Calle 123",
                "+573001234567",
                "http://logo.com/logo.png"
        );
    }

    @Test
    void shouldCallMapperAndServiceWhenSavingRestaurant() {
        when(restaurantRequestMapper.toRestaurant(restaurantRequestDto)).thenReturn(restaurant);

        restaurantHandler.saveRestaurant(restaurantRequestDto);

        verify(restaurantRequestMapper).toRestaurant(restaurantRequestDto);
        verify(restaurantServicePort).saveRestaurant(restaurant);
    }

    @Test
    public void validate_nit_returns_service_result() {
        String expectedResult = "123456789";
        when(restaurantServicePort.validateNit()).thenReturn(expectedResult);

        String result = restaurantHandler.validateNit();

        assertEquals(expectedResult, result);
        verify(restaurantServicePort).validateNit();
    }

    @Test
    public void validate_nit_handles_null_response() {
        when(restaurantServicePort.validateNit()).thenReturn(null);

        String result = restaurantHandler.validateNit();

        assertNull(result);
        verify(restaurantServicePort).validateNit();
    }

    @Test
    public void test_list_restaurants_returns_paginated_list() {
        // Arrange
        int page = 1;
        int size = 10;

        restaurant.setNombre("Restaurant 1");
        Restaurant restaurant2 = new Restaurant(
                "123456789",
                "987654321",
                "Mi Restaurante",
                "Calle 123",
                "+573001234567",
                "http://logo.com/logo.png"
        );
        restaurant2.setNombre("Restaurant 2");

        List<Restaurant> restaurants = Arrays.asList(restaurant, restaurant2);
        Page<Restaurant> restaurantPage = new PageImpl<>(restaurants);

        when(restaurantServicePort.listRestaurants(page, size)).thenReturn(restaurantPage);
        when(restaurantRequestMapper.toResponseDto(any(Restaurant.class)))
                .thenAnswer(i -> {
                    Restaurant r = i.getArgument(0);
                    return new RestaurantResponseDto(r.getNombre(), null);
                });

        // Act
        Page<RestaurantResponseDto> result = restaurantHandler.listRestaurants(page, size);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals("Restaurant 1", result.getContent().get(0).getNombre());
        assertEquals("Restaurant 2", result.getContent().get(1).getNombre());

        verify(restaurantServicePort).listRestaurants(page, size);
        verify(restaurantRequestMapper, times(2)).toResponseDto(any(Restaurant.class));
    }

    @Test
    public void test_list_restaurants_handles_first_page() {
        // Arrange
        int page = 0;
        int size = 5;

        restaurant.setNombre("First Restaurant");

        List<Restaurant> restaurants = Collections.singletonList(restaurant);
        Page<Restaurant> restaurantPage = new PageImpl<>(restaurants);

        when(restaurantServicePort.listRestaurants(page, size)).thenReturn(restaurantPage);
        when(restaurantRequestMapper.toResponseDto(any(Restaurant.class)))
                .thenReturn(new RestaurantResponseDto("First Restaurant", null));

        // Act
        Page<RestaurantResponseDto> result = restaurantHandler.listRestaurants(page, size);

        // Assert
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalElements());
        assertEquals("First Restaurant", result.getContent().get(0).getNombre());

        verify(restaurantServicePort).listRestaurants(page, size);
        verify(restaurantRequestMapper).toResponseDto(any(Restaurant.class));
    }
}
