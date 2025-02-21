package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.mapper.IRestaurantRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IRestaurantServicePort;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        restaurantRequestDto = RestaurantRequestDto.builder()
                .nit("123456789")
                .cedulaPropietario("987654321")
                .nombre("Mi Restaurante")
                .direccion("Calle 123")
                .telefono("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .build();

        restaurant = Restaurant.builder()
                .nit("123456789")
                .cedulaPropietario("987654321")
                .nombre("Mi Restaurante")
                .direccion("Calle 123")
                .telefono("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .build();
    }

    @Test
    void shouldCallMapperAndServiceWhenSavingRestaurant() {
        when(restaurantRequestMapper.toRestaurant(restaurantRequestDto)).thenReturn(restaurant);

        restaurantHandler.saveRestaurant(restaurantRequestDto);

        verify(restaurantRequestMapper).toRestaurant(restaurantRequestDto);
        verify(restaurantServicePort).saveRestaurant(restaurant);
    }
}
