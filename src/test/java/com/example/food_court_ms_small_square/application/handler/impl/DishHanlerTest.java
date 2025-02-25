package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.DishResponseDto;
import com.example.food_court_ms_small_square.application.mapper.IDishRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.model.Category;
import com.example.food_court_ms_small_square.domain.model.Dish;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void test_update_dish_status_successfully() {
        // Arrange
        Long dishId = 1L;
        UpdateDishStatusRequestDto requestDto = new UpdateDishStatusRequestDto(true);

        // Act
        dishHandler.updateDishStatus(dishId, requestDto);

        // Assert
        verify(dishServicePort).updateDishStatus(dishId, true);
    }

    @Test
    public void test_list_dishes_with_valid_filters() {
        String restauranteNit = "123456789";
        Boolean activo = true;
        Long categoriaId = 1L;
        int page = 0;
        int size = 10;

        Dish dish = new Dish(1L, "Pizza", null, "Delicious pizza", 12.5f, "123456", "http://image.url", true);
        DishResponseDto dishResponseDto = new DishResponseDto();
        Page<Dish> dishPage = new PageImpl<>(List.of(dish));

        when(dishServicePort.listDishesByFilters(restauranteNit, activo, categoriaId, page, size))
                .thenReturn(dishPage);
        when(dishRequestMapper.toResponseDto(dish)).thenReturn(dishResponseDto);

        // Act
        Page<DishResponseDto> result = dishHandler.listDishesByFilters(restauranteNit, activo, categoriaId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(dishServicePort).listDishesByFilters(restauranteNit, activo, categoriaId, page, size);
        verify(dishRequestMapper).toResponseDto(dish);
    }

    @Test
    public void test_list_dishes_with_null_filters() {
        // Arrange
        IDishServicePort dishServicePort = mock(IDishServicePort.class);
        IDishRequestMapper dishRequestMapper = mock(IDishRequestMapper.class);
        DishHandler dishHandler = new DishHandler(dishServicePort, dishRequestMapper);

        String restauranteNit = null;
        Boolean activo = null;
        Long categoriaId = null;
        int page = 0;
        int size = 10;

        Dish dish = new Dish(1L, "Pizza", null, "Delicious pizza", 12.5f, "123456", "http://image.url", true);
        DishResponseDto dishResponseDto = new DishResponseDto();
        Page<Dish> dishPage = new PageImpl<>(List.of(dish));

        when(dishServicePort.listDishesByFilters(null, null, null, page, size))
                .thenReturn(dishPage);
        when(dishRequestMapper.toResponseDto(dish)).thenReturn(dishResponseDto);

        // Act
        Page<DishResponseDto> result = dishHandler.listDishesByFilters(restauranteNit, activo, categoriaId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(dishServicePort).listDishesByFilters(null, null, null, page, size);
        verify(dishRequestMapper).toResponseDto(dish);
    }
}
