package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RestaurantJpaAdapterTest {

    @Mock
    private IRestaurantRepository restaurantRepository;

    @Mock
    private IRestaurantEntityMapper restaurantEntityMapper;

    @InjectMocks
    private RestaurantJpaAdapter restaurantJpaAdapter;

    @Test
    public void saveValidRestaurantReturnsMappedModel() {
        // Arrange
        Restaurant restaurant = new Restaurant(
                "123456",
                "98765",
                "Test Restaurant",
                "Test Address",
                "1234567890",
                "http://test.com/logo.png"
        );

        RestaurantEntity restaurantEntity = new RestaurantEntity(
                "123456", "98765", "Test Restaurant",
                "Test Address", "1234567890", "http://test.com/logo.png"
        );

        when(restaurantEntityMapper.toEntity(restaurant)).thenReturn(restaurantEntity);
        when(restaurantRepository.save(restaurantEntity)).thenReturn(restaurantEntity);
        when(restaurantEntityMapper.toModel(restaurantEntity)).thenReturn(restaurant);

        // Act
        Restaurant result = restaurantJpaAdapter.saveRestaurant(restaurant);

        // Assert
        assertNotNull(result);
        assertEquals(restaurant.getNit(), result.getNit());
        assertEquals(restaurant.getNombre(), result.getNombre());
        verify(restaurantRepository).save(restaurantEntity);
        verify(restaurantEntityMapper).toEntity(restaurant);
        verify(restaurantEntityMapper).toModel(restaurantEntity);
    }

    @Test
    public void restaurantExistsReturnsTrueWhenRestaurantIsFound() {
        // Arrange
        String nit = "123456";
        when(restaurantRepository.existsById(nit)).thenReturn(true);

        // Act
        boolean result = restaurantJpaAdapter.restaurantExists(nit);

        // Assert
        assertTrue(result);
        verify(restaurantRepository).existsById(nit);
    }

    @Test
    public void restaurantExistsReturnsFalseWhenRestaurantIsNotFound() {
        // Arrange
        String nit = "999999";
        when(restaurantRepository.existsById(nit)).thenReturn(false);

        // Act
        boolean result = restaurantJpaAdapter.restaurantExists(nit);

        // Assert
        assertFalse(result);
        verify(restaurantRepository).existsById(nit);
    }
}
