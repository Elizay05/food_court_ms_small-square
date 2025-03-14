package com.example.food_court_ms_small_square.domain.usecase;


import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.exception.OwnerInvalidException;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExistsException;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.domain.spi.IRestaurantPersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
import org.junit.jupiter.api.BeforeEach;
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
public class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserValidationPersistencePort userValidationPersistencePort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant(
                "123456789",
                "987654321",
                "Mi Restaurante",
                "Calle 123",
                "+573001234567",
                "http://logo.com/logo.png"
        );
    }

    @Test
    void shouldThrowExceptionWhenRestaurantAlreadyExists() {
        when(restaurantPersistencePort.restaurantExists(restaurant.getNit())).thenReturn(true);

        ElementAlreadyExistsException exception = assertThrows(ElementAlreadyExistsException.class, () -> {
            restaurantUseCase.saveRestaurant(restaurant);
        });

        assertEquals("El restaurante ya existe", exception.getMessage());

        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowExceptionWhenOwnerIsInvalid() {
        when(restaurantPersistencePort.restaurantExists(restaurant.getNit())).thenReturn(false);
        when(userValidationPersistencePort.isValidOwner(restaurant.getCedulaPropietario())).thenReturn(false);

        OwnerInvalidException exception = assertThrows(OwnerInvalidException.class, () -> {
            restaurantUseCase.saveRestaurant(restaurant);
        });

        assertEquals("El propietario no es valido", exception.getMessage());

        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void shouldSaveRestaurantWhenValid() {
        when(restaurantPersistencePort.restaurantExists(restaurant.getNit())).thenReturn(false);
        when(userValidationPersistencePort.isValidOwner(restaurant.getCedulaPropietario())).thenReturn(true);

        restaurantUseCase.saveRestaurant(restaurant);

        verify(restaurantPersistencePort).saveRestaurant(restaurant);
    }

    @Test
    public void test_validate_nit_returns_valid_string() {
        String expectedNit = "123456789";
        when(restaurantPersistencePort.validateNit()).thenReturn(expectedNit);

        String actualNit = restaurantUseCase.validateNit();

        assertEquals(expectedNit, actualNit);
        verify(restaurantPersistencePort).validateNit();
    }

    @Test
    public void test_validate_nit_handles_null_return() {
        when(restaurantPersistencePort.validateNit()).thenReturn(null);

        String actualNit = restaurantUseCase.validateNit();

        assertNull(actualNit);
        verify(restaurantPersistencePort).validateNit();
    }

    @Test
    public void test_list_restaurants_returns_valid_page() {
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "name", true);
        List<Restaurant> restaurants = Arrays.asList(new Restaurant("123456789", "987654321", "Mi Restaurante", "Calle 123", "+573001234567", "http://logo.com/logo.png"),
                new Restaurant("987654321", "123456789", "Mi Restaurante 2", "Calle 123", "+573001234567", "http://logo.com/logo.png"));
        Page<Restaurant> expectedPage = new Page<>(restaurants, 1, 2);

        when(restaurantPersistencePort.listRestaurants(pageRequest)).thenReturn(expectedPage);

        // Act
        Page<Restaurant> result = restaurantUseCase.listRestaurants(pageRequest);

        // Assert
        assertEquals(expectedPage.getContent(), result.getContent());
        assertEquals(expectedPage.getTotalPages(), result.getTotalPages());
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        verify(restaurantPersistencePort).listRestaurants(pageRequest);
    }

    @Test
    public void test_list_restaurants_with_large_page_number() {
        PageRequestDto pageRequest = new PageRequestDto(999, 10, "name", true);
        Page<Restaurant> emptyPage = new Page<>(Collections.emptyList(), 1, 0);

        when(restaurantPersistencePort.listRestaurants(pageRequest)).thenReturn(emptyPage);

        // Act
        Page<Restaurant> result = restaurantUseCase.listRestaurants(pageRequest);

        // Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getTotalElements());
        verify(restaurantPersistencePort).listRestaurants(pageRequest);
    }
}
