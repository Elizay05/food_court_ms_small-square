package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.exception.InvalidArgumentsException;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.spi.IDishPersistencePort;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
}

