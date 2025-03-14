package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.RestaurantResponseDto;
import com.example.food_court_ms_small_square.application.handler.impl.RestaurantHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RestaurantRestControllerTest {

    @Mock
    private RestaurantHandler restaurantHandler;

    @InjectMocks
    private RestaurantRestController controller;

    @Test
    public void test_valid_restaurant_request_returns_created_status() {
        RestaurantRequestDto requestDto = new RestaurantRequestDto("123456789", "987654321", "Test Restaurant", "Test Address","+573001234567", "http://test-logo.com");

        // Act
        ResponseEntity<Void> response = controller.saveObject(requestDto);

        // Assert
        verify(restaurantHandler).saveRestaurant(requestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_validate_nit_with_owner_role_returns_ok() {
        // Arrange
        String expectedNit = "123456789";
        when(restaurantHandler.validateNit()).thenReturn(expectedNit);

        // Act
        ResponseEntity<String> response = controller.validateNit();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNit, response.getBody());
        verify(restaurantHandler).validateNit();
    }

    @Test
    public void test_list_restaurants_default_pagination() {
        List<RestaurantResponseDto> restaurants = Arrays.asList(
                new RestaurantResponseDto(),
                new RestaurantResponseDto()
        );

        PageResponseDto<RestaurantResponseDto> expectedPage = new PageResponseDto<>(
                restaurants,
                1,
                2L
        );

        when(restaurantHandler.listRestaurants(0, 10)).thenReturn(expectedPage);

        // Act
        ResponseEntity<PageResponseDto<RestaurantResponseDto>> response =
                controller.listRestaurants(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
        verify(restaurantHandler).listRestaurants(0, 10);
    }
}
