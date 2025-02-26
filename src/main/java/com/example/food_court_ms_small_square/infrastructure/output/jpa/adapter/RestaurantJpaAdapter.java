package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.domain.spi.IRestaurantPersistencePort;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import com.example.food_court_ms_small_square.infrastructure.exception.NoSuchElementException;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

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
    public Page<Restaurant> listRestaurants(PageRequestDto pageRequest) {
        Sort sort = pageRequest.isAscending() ? Sort.by(pageRequest.getSortBy()).ascending()
                : Sort.by(pageRequest.getSortBy()).descending();
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), sort);

        org.springframework.data.domain.Page<RestaurantEntity> restaurantPage = restaurantRepository.findAll(pageable);

        List<Restaurant> restaurants = restaurantPage.getContent().stream()
                .map(restaurantEntityMapper::toModel)
                .collect(Collectors.toList());

        return new Page<>(
                restaurants,
                restaurantPage.getTotalPages(),
                restaurantPage.getTotalElements()
        );
    }
}
