package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.DishResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.mapper.IDishRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.model.Category;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.model.Page;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
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
    public void test_list_dishes_with_valid_filters_returns_paginated_response() {
        // Arrange
        IDishServicePort dishServicePort = mock(IDishServicePort.class);
        IDishRequestMapper dishRequestMapper = mock(IDishRequestMapper.class);
        DishHandler dishHandler = new DishHandler(dishServicePort, dishRequestMapper);

        String restauranteNit = "123";
        Boolean activo = true;
        Long categoriaId = 1L;
        int page = 0;
        int size = 10;

        List<Dish> dishes = Arrays.asList(new Dish(1L, null, null, null, null, null, null, null), new Dish(1L, null, null, null, null, null, null, null));
        Page<Dish> dishPage = new Page<>(dishes, 1, 2L);
        List<DishResponseDto> dishDtos = Arrays.asList(new DishResponseDto(), new DishResponseDto());

        when(dishServicePort.listDishesByFilters(eq(restauranteNit), eq(activo), eq(categoriaId),
                any(PageRequestDto.class))).thenReturn(dishPage);
        when(dishRequestMapper.toResponseDto(any(Dish.class))).thenReturn(new DishResponseDto());

        // Act
        PageResponseDto<DishResponseDto> result = dishHandler.listDishesByFilters(restauranteNit, activo, categoriaId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(2L, result.getTotalElements());
    }

    @Test
    public void test_list_dishes_with_null_filters_returns_paginated_response() {
        // Arrange
        IDishServicePort dishServicePort = mock(IDishServicePort.class);
        IDishRequestMapper dishRequestMapper = mock(IDishRequestMapper.class);
        DishHandler dishHandler = new DishHandler(dishServicePort, dishRequestMapper);

        String restauranteNit = null;
        Boolean activo = null;
        Long categoriaId = null;
        int page = 0;
        int size = 10;

        List<Dish> dishes = Arrays.asList(new Dish(1L, null, null, null, null, null, null, null));
        Page<Dish> dishPage = new Page<>(dishes, 1, 1L);

        when(dishServicePort.listDishesByFilters(eq(restauranteNit), eq(activo), eq(categoriaId),
                any(PageRequestDto.class))).thenReturn(dishPage);
        when(dishRequestMapper.toResponseDto(any(Dish.class))).thenReturn(new DishResponseDto());

        // Act
        PageResponseDto<DishResponseDto> result = dishHandler.listDishesByFilters(restauranteNit, activo, categoriaId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(1L, result.getTotalElements());
    }
}
