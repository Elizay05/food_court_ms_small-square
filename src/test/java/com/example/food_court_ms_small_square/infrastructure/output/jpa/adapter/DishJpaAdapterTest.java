package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.infrastructure.exception.NoSuchElementException;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IDishRespository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

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

    @Test
    public void updateDish_updates_only_description_when_price_is_null() {
        // Arrange
        Long id = 1L;
        String nuevaDescripcion = "Nueva descripción";
        DishEntity existingDish = new DishEntity();
        existingDish.setId(id);
        existingDish.setDescripcion("Descripción antigua");
        existingDish.setPrecio(15.0f);

        when(entityRepository.findById(id)).thenReturn(Optional.of(existingDish));

        // Act
        adapter.updateDish(id, null, nuevaDescripcion);

        // Assert
        assertEquals(nuevaDescripcion, existingDish.getDescripcion());
        assertEquals(15.0f, existingDish.getPrecio());
        verify(entityRepository).save(existingDish);
    }

    @Test
    public void updateDish_updates_only_price_when_description_is_null() {
        // Arrange
        Long id = 1L;
        Float nuevoPrecio = 20.0f;
        DishEntity existingDish = new DishEntity();
        existingDish.setId(id);
        existingDish.setDescripcion("Descripción antigua");
        existingDish.setPrecio(15.0f);

        when(entityRepository.findById(id)).thenReturn(Optional.of(existingDish));

        // Act
        adapter.updateDish(id, nuevoPrecio, null);

        // Assert
        assertEquals(nuevoPrecio, existingDish.getPrecio());
        assertEquals("Descripción antigua", existingDish.getDescripcion());
        verify(entityRepository).save(existingDish);
    }

    @Test
    public void updateDish_updates_both_price_and_description() {
        // Arrange
        Long id = 1L;
        Float nuevoPrecio = 20.0f;
        String nuevaDescripcion = "Nueva descripción";
        DishEntity existingDish = new DishEntity();
        existingDish.setId(id);
        existingDish.setDescripcion("Descripción antigua");
        existingDish.setPrecio(15.0f);

        when(entityRepository.findById(id)).thenReturn(Optional.of(existingDish));

        // Act
        adapter.updateDish(id, nuevoPrecio, nuevaDescripcion);

        // Assert
        assertEquals(nuevaDescripcion, existingDish.getDescripcion());
        assertEquals(nuevoPrecio, existingDish.getPrecio());
        verify(entityRepository).save(existingDish);
    }

    @Test
    public void updateDish_throws_exception_when_dish_not_found() {
        // Arrange
        Long id = 99L;
        when(entityRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            adapter.updateDish(id, 25.0f, "Nueva descripción");
        });

        verify(entityRepository, never()).save(any());
    }
}
