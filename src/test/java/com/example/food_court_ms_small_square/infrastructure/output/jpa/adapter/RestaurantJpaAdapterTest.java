package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
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
        CustomUserDetails userDetails = new CustomUserDetails("username", documentNumber, "", Collections.emptyList());

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
    public void test_list_restaurants_ascending_sort() {
        // Arrange
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "nombre", true);

        List<RestaurantEntity> restaurantEntities = Arrays.asList(
                new RestaurantEntity("123", "456", "Restaurant A", "Address 1", "123456", "url1"),
                new RestaurantEntity("789", "012", "Restaurant B", "Address 2", "789012", "url2")
        );

        Page<RestaurantEntity> springPage = new PageImpl<>(restaurantEntities, PageRequest.of(0, 10, Sort.by("nombre").ascending()), 2);

        when(restaurantRepository.findAll(any(Pageable.class))).thenReturn(springPage);
        when(restaurantEntityMapper.toModel(any(RestaurantEntity.class)))
                .thenReturn(new Restaurant("123", "456", "Restaurant A", "Address 1", "123456", "url1"))
                .thenReturn(new Restaurant("789", "012", "Restaurant B", "Address 2", "789012", "url2"));

        // Act
        com.example.food_court_ms_small_square.domain.model.Page<Restaurant> result = restaurantJpaAdapter.listRestaurants(pageRequest);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("Restaurant A", result.getContent().get(0).getNombre());
        assertEquals("Restaurant B", result.getContent().get(1).getNombre());
        verify(restaurantRepository).findAll(any(Pageable.class));
    }

    @Test
    public void test_list_restaurants_descending_sort() {
        // Arrange
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "nombre", false);

        List<RestaurantEntity> restaurantEntities = Arrays.asList(
                new RestaurantEntity("789", "012", "Restaurant B", "Address 2", "789012", "url2"),
                new RestaurantEntity("123", "456", "Restaurant A", "Address 1", "123456", "url1")
        );

        Page<RestaurantEntity> springPage = new PageImpl<>(restaurantEntities, PageRequest.of(0, 10, Sort.by("nombre").descending()), 2);

        when(restaurantRepository.findAll(any(Pageable.class))).thenReturn(springPage);
        when(restaurantEntityMapper.toModel(any(RestaurantEntity.class)))
                .thenReturn(new Restaurant("789", "012", "Restaurant B", "Address 2", "789012", "url2"))
                .thenReturn(new Restaurant("123", "456", "Restaurant A", "Address 1", "123456", "url1"));

        // Act
        com.example.food_court_ms_small_square.domain.model.Page<Restaurant> result = restaurantJpaAdapter.listRestaurants(pageRequest);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("Restaurant B", result.getContent().get(0).getNombre());
        assertEquals("Restaurant A", result.getContent().get(1).getNombre());
        verify(restaurantRepository).findAll(any(Pageable.class));
    }

    @Test
    public void test_list_restaurants_empty_list() {
        // Arrange
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "nombre", true);

        List<RestaurantEntity> emptyList = Collections.emptyList();
        Page<RestaurantEntity> emptyPage = new PageImpl<>(emptyList, PageRequest.of(0, 10, Sort.by("nombre").ascending()), 0);

        when(restaurantRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        com.example.food_court_ms_small_square.domain.model.Page<Restaurant> result = restaurantJpaAdapter.listRestaurants(pageRequest);

        // Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        verify(restaurantRepository).findAll(any(Pageable.class));
    }
}
