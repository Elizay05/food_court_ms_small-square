package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.exception.InvalidArgumentsException;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.spi.IDishPersistencePort;
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
public class DishUseCaseTest {

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @InjectMocks
    private DishUseCase dishUseCase;

    @Test
    public void test_save_dish_sets_activo_true() {

        Dish dish = new Dish(
                null,
                "Pizza",
                null,
                "Delicious pizza",
                15.99f,
                null,
                null,
                false
        );

        dishUseCase.saveDish(dish);

        verify(dishPersistencePort).saveDish(dish);
        assertTrue(dish.getActivo());
    }

    @Test
    public void test_save_dish_null_input() {

        assertThrows(NullPointerException.class, () -> {
            dishUseCase.saveDish(null);
        });

        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    public void test_update_dish_with_valid_price_and_description() {
        Long id = 1L;
        Float price = 10.5F;
        String description = "New description";

        // Act
        dishUseCase.updateDish(id, price, description);

        // Assert
        verify(dishPersistencePort).updateDish(id, price, description);
    }

    @Test
    public void test_update_dish_with_null_price_and_description() {
        Long id = 1L;

        // Act & Assert
        assertThrows(InvalidArgumentsException.class, () -> {
            dishUseCase.updateDish(id, null, null);
        });
        verify(dishPersistencePort, never()).updateDish(any(), any(), any());
    }

    @Test
    public void test_update_dish_status_enabled_success() {
        // Arrange
        Long dishId = 1L;
        Boolean enabled = true;

        // Act
        dishUseCase.updateDishStatus(dishId, enabled);

        // Assert
        verify(dishPersistencePort).updateDishStatus(dishId, enabled);
    }

    @Test
    public void test_list_dishes_by_filters_returns_page_with_content() {
        // Arrange
        String restauranteNit = "123456789";
        Boolean activo = true;
        Long categoriaId = 1L;
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "name", true);

        List<Dish> dishes = Arrays.asList(new Dish(1L, null, null, null, null, null, null, null), new Dish(2L, null, null, null, null, null, null, null));
        Page<Dish> expectedPage = new Page<>(dishes, 1, 2L);

        when(dishPersistencePort.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto))
                .thenReturn(expectedPage);

        // Act
        Page<Dish> result = dishUseCase.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(2L, result.getTotalElements());

        verify(dishPersistencePort).listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);
    }

    @Test
    public void test_list_dishes_by_filters_returns_empty_page() {
        // Arrange
        String restauranteNit = "123456789";
        Boolean activo = true;
        Long categoriaId = 1L;
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "name", true);

        List<Dish> emptyDishes = Collections.emptyList();
        Page<Dish> emptyPage = new Page<>(emptyDishes, 0, 0L);

        when(dishPersistencePort.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto))
                .thenReturn(emptyPage);

        // Act
        Page<Dish> result = dishUseCase.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);

        // Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalPages());
        assertEquals(0L, result.getTotalElements());

        verify(dishPersistencePort).listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);
    }
}

