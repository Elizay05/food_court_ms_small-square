package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.mapper.IDishRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.model.Category;
import com.example.food_court_ms_small_square.domain.model.Dish;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DishHanlerTest {

    @Mock
    private IDishServicePort dishServicePort;

    @Mock
    private IDishRequestMapper dishRequestMapper;

    @InjectMocks
    private DishHandler dishHandler;

    @Test
    void testSaveDish() {
        DishRequestDto dishRequestDto = new DishRequestDto(
                "Pizza", 12.5f, "Delicious pizza", "http://image.url", 1, "123456"
        );

        Category category = new Category(1L, null, null);
        Dish expectedDish = new Dish(
                null,
                "Pizza",
                category,
                "Delicious pizza",
                12.5f,
                "123456",
                "http://image.url",
                null
        );

        Mockito.when(dishRequestMapper.toDish(dishRequestDto)).thenReturn(expectedDish);

        dishHandler.saveDish(dishRequestDto);

        verify(dishRequestMapper).toDish(dishRequestDto);
        verify(dishServicePort).saveDish(expectedDish);
    }

    @Test
    public void test_update_dish_with_valid_data() {

        UpdateDishRequestDto updateDishRequestDto = new UpdateDishRequestDto();
        updateDishRequestDto.setId(1);
        updateDishRequestDto.setPrice(15.99f);
        updateDishRequestDto.setDescription("Updated dish description");

        // Act
        dishHandler.updateDish(updateDishRequestDto);

        // Assert
        verify(dishServicePort).updateDish(1L, 15.99f, "Updated dish description");
    }

    @Test
    public void test_update_dish_with_null_request() {

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            dishHandler.updateDish(null);
        });

        verify(dishServicePort, never()).updateDish(anyLong(), anyFloat(), anyString());
    }
}
