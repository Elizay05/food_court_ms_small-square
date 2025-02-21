package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.RestaurantRequestDto;
import com.example.food_court_ms_small_square.application.handler.impl.RestaurantHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        RestaurantRequestDto requestDto = RestaurantRequestDto.builder()
                .nit("123456789")
                .cedulaPropietario("987654321")
                .nombre("Test Restaurant")
                .direccion("Test Address")
                .telefono("+573001234567")
                .urlLogo("http://test-logo.com")
                .build();

        // Act
        ResponseEntity<Void> response = controller.saveObject(requestDto);

        // Assert
        verify(restaurantHandler).saveRestaurant(requestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
