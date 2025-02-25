package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.exception.InvalidArgumentsException;
import com.example.food_court_ms_small_square.domain.model.Category;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.spi.IDishPersistencePort;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.Arrays;
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
    public void test_list_dishes_with_valid_filters() {
        String restaurantNit = "123456";
        Boolean active = true;
        Long categoryId = 1L;
        int page = 0;
        int size = 10;

        List<Dish> dishes = Arrays.asList(
                new Dish(1L, "Dish1", new Category(1L, "Category1", "desc1"), "desc1", 10.0f, restaurantNit, "url1", true),
                new Dish(2L, "Dish2", new Category(2L, "Category2", "desc2"), "desc2", 20.0f, restaurantNit, "url2", true)
        );

        Page<Dish> expectedPage = new PageImpl<>(dishes);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("nombre").ascending());

        when(dishPersistencePort.listDishesByFilters(restaurantNit, active, categoryId, expectedPageable))
                .thenReturn(expectedPage);

        // Act
        Page<Dish> result = dishUseCase.listDishesByFilters(restaurantNit, active, categoryId, page, size);

        // Assert
        assertEquals(expectedPage, result);
        verify(dishPersistencePort).listDishesByFilters(restaurantNit, active, categoryId, expectedPageable);
    }
}

