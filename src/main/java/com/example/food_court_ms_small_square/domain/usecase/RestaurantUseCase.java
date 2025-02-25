package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.domain.api.IRestaurantServicePort;
import com.example.food_court_ms_small_square.domain.exception.OwnerInvalidException;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExistsException;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.domain.spi.IRestaurantPersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserValidationPersistencePort userValidationPersistencePort;

    @Override
    public void saveRestaurant(Restaurant restaurant) {
        if (Boolean.TRUE.equals(restaurantPersistencePort.restaurantExists(restaurant.getNit())))
            throw new ElementAlreadyExistsException("El restaurante ya existe");

        if (!userValidationPersistencePort.isValidOwner(restaurant.getCedulaPropietario())) {
            throw new OwnerInvalidException("El propietario no es valido");
        }

        userValidationPersistencePort.updateNit(restaurant.getCedulaPropietario(), restaurant.getNit());
        restaurantPersistencePort.saveRestaurant(restaurant);
    }

    @Override
    public String validateNit(){
        return restaurantPersistencePort.validateNit();
    }

    @Override
    public Page<Restaurant> listRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return restaurantPersistencePort.listRestaurants(pageable);
    }
}
