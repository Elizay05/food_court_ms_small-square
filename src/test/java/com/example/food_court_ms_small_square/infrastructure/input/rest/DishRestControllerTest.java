package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.DishResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.handler.IDishHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DishRestControllerTest {

    @Mock
    private IDishHandler dishHandler;

    @InjectMocks
    private DishRestController dishRestController;

    @Test
    public void test_valid_dish_request_returns_created_status() {
        // Arrange
        DishRequestDto dishRequestDto = new DishRequestDto(
                "Pizza",
                15.99f,
                "Delicious pizza",
                "http://image.url",
                1,
                "123456789"
        );

        // Act
        ResponseEntity<Void> response = dishRestController.saveDish(dishRequestDto);

        // Assert
        verify(dishHandler).saveDish(dishRequestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_valid_update_request_returns_accepted() {
        // Arrange
        UpdateDishRequestDto updateRequest = new UpdateDishRequestDto(1, 15.99f, "Updated description");

        // Act
        ResponseEntity<Void> response = dishRestController.updateDish(updateRequest);

        // Assert
        verify(dishHandler).updateDish(updateRequest);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void test_update_dish_status_success() {
        // Arrange
        Long dishId = 1L;
        UpdateDishStatusRequestDto requestDto = new UpdateDishStatusRequestDto(true);

        doNothing().when(dishHandler).updateDishStatus(dishId, requestDto);

        // Act
        ResponseEntity<Void> response = dishRestController.updateDishStatus(dishId, requestDto);

        // Assert
        verify(dishHandler).updateDishStatus(dishId, requestDto);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void test_list_dishes_with_valid_nit_returns_paginated_response() {
        // Arrange
        String validNit = "123456789";
        int page = 0;
        int size = 10;
        Long categoryId = null;

        List<DishResponseDto> dishList = Arrays.asList(new DishResponseDto(), new DishResponseDto());
        PageResponseDto<DishResponseDto> expectedResponse = new PageResponseDto<>(dishList, 1, 2);

        when(dishHandler.listDishesByFilters(validNit, true, categoryId, page, size))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<PageResponseDto<DishResponseDto>> response = dishRestController.listDishes(
                validNit, categoryId, page, size);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());

        verify(dishHandler).listDishesByFilters(validNit, true, categoryId, page, size);
    }

    @Test
    public void test_list_dishes_with_invalid_nit_throws_exception() {
        // Arrange
        String invalidNit = "";
        int page = 0;
        int size = 10;
        Long categoryId = null;

        when(dishHandler.listDishesByFilters(invalidNit, true, categoryId, page, size))
                .thenThrow(new IllegalArgumentException("Invalid restauranteNit"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            dishRestController.listDishes(invalidNit, categoryId, page, size);
        });

        verify(dishHandler).listDishesByFilters(invalidNit, true, categoryId, page, size);
    }
}
