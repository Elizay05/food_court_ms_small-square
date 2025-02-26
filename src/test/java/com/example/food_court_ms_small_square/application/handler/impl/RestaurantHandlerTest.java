package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.RestaurantResponseDto;
import com.example.food_court_ms_small_square.application.mapper.IRestaurantRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IRestaurantServicePort;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    public void test_list_restaurants_returns_mapped_response() {
        // Arrange
        IRestaurantServicePort restaurantServicePort = mock(IRestaurantServicePort.class);
        IRestaurantRequestMapper restaurantRequestMapper = mock(IRestaurantRequestMapper.class);
        RestaurantHandler restaurantHandler = new RestaurantHandler(restaurantServicePort, restaurantRequestMapper);

        List<Restaurant> restaurants = Arrays.asList(
                new Restaurant("111", "222", "Restaurant 1", "CLL 123", "123456789", "logo1.jpg"),
                new Restaurant("333", "444", "Restaurant 2", "CLL 456", "987654321", "logo2.jpg 2")
        );

        Page<Restaurant> page = new Page<>(restaurants, 1, 2);
        when(restaurantServicePort.listRestaurants(any(PageRequestDto.class))).thenReturn(page);

        when(restaurantRequestMapper.toResponseDto(any(Restaurant.class)))
                .thenReturn(new RestaurantResponseDto("Restaurant 1", "logo1.jpg"))
                .thenReturn(new RestaurantResponseDto("Restaurant 2", "logo2.jpg"));

        // Act
        PageResponseDto<RestaurantResponseDto> result = restaurantHandler.listRestaurants(0, 10);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        assertEquals("Restaurant 1", result.getContent().get(0).getNombre());
        assertEquals("logo2.jpg", result.getContent().get(1).getUrlLogo());
    }

    @Test
    public void test_list_restaurants_returns_empty_response() {
        // Arrange
        IRestaurantServicePort restaurantServicePort = mock(IRestaurantServicePort.class);
        IRestaurantRequestMapper restaurantRequestMapper = mock(IRestaurantRequestMapper.class);
        RestaurantHandler restaurantHandler = new RestaurantHandler(restaurantServicePort, restaurantRequestMapper);

        Page<Restaurant> emptyPage = new Page<>(Collections.emptyList(), 0, 0);
        when(restaurantServicePort.listRestaurants(any(PageRequestDto.class))).thenReturn(emptyPage);

        // Act
        PageResponseDto<RestaurantResponseDto> result = restaurantHandler.listRestaurants(0, 10);

        // Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalPages());
        assertEquals(0, result.getTotalElements());
    }
}
