package com.example.food_court_ms_small_square.domain.usecase;


import com.example.food_court_ms_small_square.domain.exception.OwnerInvalidException;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExistsException;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.domain.spi.IRestaurantPersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

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
}
