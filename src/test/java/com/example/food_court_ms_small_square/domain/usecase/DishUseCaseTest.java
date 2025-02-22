package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExists;
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
    public void test_save_dish_throws_exception_when_dish_exists() {
        // Arrange
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

        when(dishPersistencePort.dishExists(dish.getNombre(), dish.getRestauranteNit())).thenReturn(true);

        // Act & Assert
        assertThrows(ElementAlreadyExists.class, () -> dishUseCase.saveDish(dish));

        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    public void test_save_dish_null_input() {

        assertThrows(NullPointerException.class, () -> {
            dishUseCase.saveDish(null);
        });

        verify(dishPersistencePort, never()).saveDish(any());
    }
}

