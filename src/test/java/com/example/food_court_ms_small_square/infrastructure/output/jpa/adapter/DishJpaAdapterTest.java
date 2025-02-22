package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IDishRespository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DishJpaAdapterTest {

    @Mock
    private IDishEntityMapper entityMapper;

    @Mock
    private IDishRespository entityRepository;

    @InjectMocks
    private DishJpaAdapter adapter;

    @Test
    public void save_dish_maps_and_saves_successfully() {
        Dish dish = new Dish(
                1L,
                "Test Dish",
                null,
                null,
                10.0f,
                null,
                null,
                null
        );

        DishEntity expectedEntity = new DishEntity();
        when(entityMapper.toEntity(dish)).thenReturn(expectedEntity);

        // Act
        adapter.saveDish(dish);

        // Assert
        verify(entityMapper).toEntity(dish);
        verify(entityRepository).save(expectedEntity);
    }

    @Test
    public void save_dish_handles_null_input() {
        when(entityMapper.toEntity(null)).thenThrow(IllegalArgumentException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adapter.saveDish(null);
        });

        verify(entityMapper).toEntity(null);
        verify(entityRepository, never()).save(any());
    }

    @Test
    public void dishExists_returns_true_when_dish_exists() {
        // Arrange
        String name = "Test Dish";
        String nit = "123456789";

        when(entityRepository.existsByNombreAndRestauranteNit(name, nit)).thenReturn(true);

        // Act
        Boolean result = adapter.dishExists(name, nit);

        // Assert
        assertTrue(result);
        verify(entityRepository).existsByNombreAndRestauranteNit(name, nit);
    }

    @Test
    public void dishExists_returns_false_when_dish_does_not_exist() {
        // Arrange
        String name = "Nonexistent Dish";
        String nit = "987654321";

        when(entityRepository.existsByNombreAndRestauranteNit(name, nit)).thenReturn(false);

        // Act
        Boolean result = adapter.dishExists(name, nit);

        // Assert
        assertFalse(result);
        verify(entityRepository).existsByNombreAndRestauranteNit(name, nit);
    }
}
