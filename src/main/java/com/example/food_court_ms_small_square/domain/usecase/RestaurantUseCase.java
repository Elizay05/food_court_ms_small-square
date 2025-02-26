package com.example.food_court_ms_small_square.domain.usecase;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.api.IRestaurantServicePort;
import com.example.food_court_ms_small_square.domain.exception.OwnerInvalidException;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExistsException;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.domain.spi.IRestaurantPersistencePort;
import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
import lombok.RequiredArgsConstructor;

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
    public Page<Restaurant> listRestaurants(PageRequestDto pageRequest) {
        Page<Restaurant> restaurantPage = restaurantPersistencePort.listRestaurants(pageRequest);
        return new Page<>(
                restaurantPage.getContent(),
                restaurantPage.getTotalPages(),
                restaurantPage.getTotalElements()
        );
    }
}
