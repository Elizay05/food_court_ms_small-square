package com.example.food_court_ms_small_square.domain.usecase;


import com.example.food_court_ms_small_square.domain.exception.OwnerInvalid;
import com.example.food_court_ms_small_square.domain.exception.RestaurantAlreadyExists;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.domain.spi.IRestaurantPersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        restaurant = Restaurant.builder()
                .nit("123456789")
                .cedulaPropietario("987654321")
                .nombre("Mi Restaurante")
                .direccion("Calle 123")
                .telefono("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .build();
    }

    @Test
    void shouldThrowExceptionWhenRestaurantAlreadyExists() {
        when(restaurantPersistencePort.restaurantExists(restaurant.getNit())).thenReturn(true);

        RestaurantAlreadyExists exception = assertThrows(RestaurantAlreadyExists.class, () -> {
            restaurantUseCase.saveRestaurant(restaurant);
        });

        assertEquals("El restaurante ya existe", exception.getMessage());

        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowExceptionWhenOwnerIsInvalid() {
        when(restaurantPersistencePort.restaurantExists(restaurant.getNit())).thenReturn(false);
        when(userValidationPersistencePort.isValidOwner(restaurant.getCedulaPropietario())).thenReturn(false);

        OwnerInvalid exception = assertThrows(OwnerInvalid.class, () -> {
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
}
