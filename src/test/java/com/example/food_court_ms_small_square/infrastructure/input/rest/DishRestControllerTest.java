package com.example.food_court_ms_small_square.infrastructure.input.rest;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.handler.IDishHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DishRestControllerTest {

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

        IDishHandler dishHandler = mock(IDishHandler.class);
        DishRestController dishRestController = new DishRestController(dishHandler);

        // Act
        ResponseEntity<Void> response = dishRestController.saveDish(dishRequestDto);

        // Assert
        verify(dishHandler).saveDish(dishRequestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
