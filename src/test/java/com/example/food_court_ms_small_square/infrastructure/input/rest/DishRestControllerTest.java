package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.DishResponseDto;
import com.example.food_court_ms_small_square.application.handler.IDishHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void test_list_dishes_with_valid_restaurant_nit() {
        String validNit = "123456789";
        int page = 0;
        int size = 10;
        Long categoryId = null;

        Page<DishResponseDto> expectedPage = new PageImpl<>(Arrays.asList(
                new DishResponseDto("Dish1", "Description1", 10.0f, "url1"),
                new DishResponseDto("Dish2", "Description2", 20.0f, "url2")
        ));

        when(dishHandler.listDishesByFilters(validNit, true, categoryId, page, size))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DishResponseDto>> response = dishRestController.listDishes(
                validNit, categoryId, page, size);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
        verify(dishHandler).listDishesByFilters(validNit, true, categoryId, page, size);
    }
}
