package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void test_validate_nit_returns_nit_when_restaurant_found() {
        // Arrange
        String documentNumber = "123456789";
        String expectedNit = "987654321";

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setNit(expectedNit);
        restaurant.setCedulaPropietario(documentNumber);

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails("username", documentNumber, Collections.emptyList());

        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(restaurantRepository.findByCedulaPropietario(documentNumber))
                .thenReturn(Optional.of(restaurant));

        // Act
        String actualNit = restaurantJpaAdapter.validateNit();

        // Assert
        assertEquals(expectedNit, actualNit);
    }

    @Test
    public void test_list_restaurants_returns_paginated_results() {
        RestaurantEntity entity1 = new RestaurantEntity();
        RestaurantEntity entity2 = new RestaurantEntity();
        List<RestaurantEntity> entities = Arrays.asList(entity1, entity2);

        Restaurant restaurant1 = new Restaurant("123", "456", "Rest1", "Address1", "123456", "logo1.jpg");
        Restaurant restaurant2 = new Restaurant("789", "012", "Rest2", "Address2", "789012", "logo2.jpg");

        Pageable pageable = PageRequest.of(0, 10);
        Page<RestaurantEntity> entityPage = new PageImpl<>(entities, pageable, entities.size());

        when(restaurantRepository.findAll(pageable)).thenReturn(entityPage);
        when(restaurantEntityMapper.toModel(entity1)).thenReturn(restaurant1);
        when(restaurantEntityMapper.toModel(entity2)).thenReturn(restaurant2);

        // Act
        Page<Restaurant> result = restaurantJpaAdapter.listRestaurants(pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        verify(restaurantRepository).findAll(pageable);
        verify(restaurantEntityMapper, times(2)).toModel(any(RestaurantEntity.class));
    }
}
