package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.domain.spi.IRestaurantPersistencePort;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import com.example.food_court_ms_small_square.infrastructure.exception.NoSuchElementException;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity restaurantEntity = restaurantRepository.save(restaurantEntityMapper.toEntity(restaurant));
        return restaurantEntityMapper.toModel(restaurantEntity);
    }

    @Override
    public Boolean restaurantExists(String nit) {
        return restaurantRepository.existsById(nit);
    }

    @Override
    public String validateNit(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String documentNumber = userDetails.getDocumentNumber();

        return restaurantRepository.findByCedulaPropietario(documentNumber)
                .map(RestaurantEntity::getNit)
                .orElseThrow(() -> new NoSuchElementException("No se encontró un restaurante con la cédula proporcionada."));
    }

    @Override
    public Page<Restaurant> listRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable)
                .map(restaurantEntityMapper::toModel);
    }
}
