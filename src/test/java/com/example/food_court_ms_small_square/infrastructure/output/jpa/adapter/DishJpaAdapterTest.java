package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExistsException;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import com.example.food_court_ms_small_square.infrastructure.exception.NoSuchElementException;
import com.example.food_court_ms_small_square.infrastructure.exception.UnauthorizedException;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IDishRespository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DishJpaAdapterTest {

    @Mock
    private IDishEntityMapper dishEntityMapper;

    @Mock
    private IDishRespository dishRepository;

    @Mock
    private IRestaurantRepository restaurantRepository;

    @InjectMocks
    private DishJpaAdapter dishJpaAdapter;

    @Test
    public void test_save_dish_success() {
        Dish dish = new Dish(1L, "Test Dish", null, "Description", 10.0f, "123", "url", true);
        RestaurantEntity restaurantEntity = new RestaurantEntity("123", "doc123", "Test Restaurant", "Address", "Phone", "logo");
        DishEntity dishEntity = new DishEntity(1L, "Test Dish", "Description", 10.0f, "123", "url", true, null);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "doc123", "12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(restaurantRepository.findById("123")).thenReturn(Optional.of(restaurantEntity));
        when(dishRepository.existsByNombreAndRestauranteNit("Test Dish", "123")).thenReturn(false);
        when(dishEntityMapper.toEntity(dish)).thenReturn(dishEntity);

        // Act
        dishJpaAdapter.saveDish(dish);

        // Assert
        verify(dishRepository).save(dishEntity);
    }

    @Test
    public void test_save_dish_restaurant_not_found() {

        Dish dish = new Dish(1L, "Test Dish", null, "Description", 10.0f, "123", "url", true);

        when(restaurantRepository.findById("123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> dishJpaAdapter.saveDish(dish));
        verify(dishRepository, never()).save(any());
    }

    @Test
    public void test_save_dish_unauthorized_user() {
        Dish dish = new Dish(1L, "Test Dish", null, "Description", 10.0f, "123", "url", true);
        RestaurantEntity restaurantEntity = new RestaurantEntity("123", "doc456", "Test Restaurant", "Address", "Phone", "logo");

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "doc789", "12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(restaurantRepository.findById("123")).thenReturn(Optional.of(restaurantEntity));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            dishJpaAdapter.saveDish(dish);
        });
    }

    @Test
    public void test_save_dish_already_exists() {
        Dish dish = new Dish(1L, "Existing Dish", null, "Description", 10.0f, "123", "url", true);
        RestaurantEntity restaurantEntity = new RestaurantEntity("123", "doc123", "Test Restaurant", "Address", "Phone", "logo");

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "doc123", "12345", Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(restaurantRepository.findById("123")).thenReturn(Optional.of(restaurantEntity));
        when(dishRepository.existsByNombreAndRestauranteNit("Existing Dish", "123")).thenReturn(true);

        // Act & Assert
        assertThrows(ElementAlreadyExistsException.class, () -> {
            dishJpaAdapter.saveDish(dish);
        });
    }

    @Test
    public void test_dish_exists_returns_true_when_dish_exists() {
        // Arrange
        String dishName = "Pizza";
        String restaurantNit = "123456789";
        when(dishRepository.existsByNombreAndRestauranteNit(dishName, restaurantNit)).thenReturn(true);

        // Act
        Boolean result = dishJpaAdapter.dishExists(dishName, restaurantNit);

        // Assert
        assertTrue(result);
        verify(dishRepository).existsByNombreAndRestauranteNit(dishName, restaurantNit);
    }

    @Test
    public void test_dish_exists_handles_empty_name() {
        // Arrange
        String emptyDishName = "";
        String restaurantNit = "123456789";
        when(dishRepository.existsByNombreAndRestauranteNit(emptyDishName, restaurantNit)).thenReturn(false);

        // Act
        Boolean result = dishJpaAdapter.dishExists(emptyDishName, restaurantNit);

        // Assert
        assertFalse(result);
        verify(dishRepository).existsByNombreAndRestauranteNit(emptyDishName, restaurantNit);
    }

    @Test
    public void test_update_dish_description_success() {

        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(1L);
        dishEntity.setRestauranteNit("123");

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCedulaPropietario("owner123");

        Authentication mockAuth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "owner123", "12345", Collections.emptyList());

        when(dishRepository.findById(1L)).thenReturn(Optional.of(dishEntity));
        when(restaurantRepository.findById("123")).thenReturn(Optional.of(restaurantEntity));
        when(securityContext.getAuthentication()).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        // Act
        dishJpaAdapter.updateDish(1L, null, "New Description");

        // Assert
        verify(dishRepository).save(dishEntity);
        assertEquals("New Description", dishEntity.getDescripcion());
    }

    @Test
    public void test_update_dish_throws_when_id_not_found() {
        when(dishRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> dishJpaAdapter.updateDish(1L, 10.0f, "New Description")
        );
        assertEquals("El plato no existe.", exception.getMessage());
    }

    @Test
    public void test_update_dish_unauthorized_exception() {
        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(1L);
        dishEntity.setRestauranteNit("123");

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCedulaPropietario("owner123");

        Authentication mockAuth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "notOwner123", "12345", Collections.emptyList());

        when(dishRepository.findById(1L)).thenReturn(Optional.of(dishEntity));
        when(restaurantRepository.findById("123")).thenReturn(Optional.of(restaurantEntity));
        when(securityContext.getAuthentication()).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            dishJpaAdapter.updateDish(1L, null, "New Description");
        });
    }

    @Test
    public void test_update_dish_price_success() {

        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(1L);
        dishEntity.setRestauranteNit("123");
        dishEntity.setPrecio(15.0f); // Precio inicial

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCedulaPropietario("owner123");

        Authentication mockAuth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", "owner123", "12345", Collections.emptyList());

        when(dishRepository.findById(1L)).thenReturn(Optional.of(dishEntity));
        when(restaurantRepository.findById("123")).thenReturn(Optional.of(restaurantEntity));
        when(securityContext.getAuthentication()).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        // Act
        dishJpaAdapter.updateDish(1L, 20.0f, null);

        // Assert
        verify(dishRepository).save(dishEntity);
        assertEquals(20.0f, dishEntity.getPrecio());
    }

    @Test
    public void test_update_dish_status_success_when_owner_updates_own_dish() {
        // Arrange
        Long dishId = 1L;
        Boolean enabled = true;
        String ownerDocumentNumber = "123";
        String restaurantNit = "456";

        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(dishId);
        dishEntity.setRestauranteNit(restaurantNit);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCedulaPropietario(ownerDocumentNumber);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", ownerDocumentNumber, "12345", Collections.emptyList());

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dishEntity));
        when(restaurantRepository.findById(restaurantNit)).thenReturn(Optional.of(restaurantEntity));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        dishJpaAdapter.updateDishStatus(dishId, enabled);

        // Assert
        verify(dishRepository).save(dishEntity);
        assertEquals(enabled, dishEntity.getActivo());
    }

    @Test
    public void test_update_dish_status_throws_exception_for_nonexistent_dish() {
        // Arrange
        Long nonExistentDishId = 999L;
        Boolean enabled = true;

        when(dishRepository.findById(nonExistentDishId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            dishJpaAdapter.updateDishStatus(nonExistentDishId, enabled);
        });

        verify(dishRepository, never()).save(any(DishEntity.class));
    }

    @Test
    public void test_update_dish_status_unauthorized_when_non_owner_attempts_update() {
        // Arrange
        Long dishId = 1L;
        Boolean enabled = true;
        String nonOwnerDocumentNumber = "789";
        String ownerDocumentNumber = "123";
        String restaurantNit = "456";

        DishEntity dishEntity = new DishEntity();
        dishEntity.setId(dishId);
        dishEntity.setRestauranteNit(restaurantNit);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCedulaPropietario(ownerDocumentNumber);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails("user", nonOwnerDocumentNumber, "12345", Collections.emptyList());

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dishEntity));
        when(restaurantRepository.findById(restaurantNit)).thenReturn(Optional.of(restaurantEntity));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            dishJpaAdapter.updateDishStatus(dishId, enabled);
        });
    }

    @Test
    public void test_list_dishes_without_category_filter() {
        // Arrange
        String restauranteNit = "123456789";
        Boolean activo = true;
        Long categoriaId = null;
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "nombre", true);

        DishEntity dish1 = new DishEntity(1L, "Dish 1", "Description 1", 10.0f, restauranteNit, "url1", true, null);
        DishEntity dish2 = new DishEntity(2L, "Dish 2", "Description 2", 20.0f, restauranteNit, "url2", true, null);
        List<DishEntity> dishEntities = Arrays.asList(dish1, dish2);

        Page<DishEntity> dishEntityPage = new PageImpl<>(dishEntities, PageRequest.of(0, 10, Sort.by("nombre").ascending()), 2);

        when(dishRepository.findByRestauranteNitAndActivo(eq(restauranteNit), eq(activo), any(Pageable.class)))
                .thenReturn(dishEntityPage);

        when(dishEntityMapper.toDish(dish1)).thenReturn(new Dish(1L, "Dish 1", null, "Description 1", 10.0f, restauranteNit, "url1", true));
        when(dishEntityMapper.toDish(dish2)).thenReturn(new Dish(2L, "Dish 2", null, "Description 2", 20.0f, restauranteNit, "url2", true));

        // Act
        com.example.food_court_ms_small_square.domain.model.Page<Dish> result = dishJpaAdapter.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        verify(dishRepository).findByRestauranteNitAndActivo(restauranteNit, activo, PageRequest.of(0, 10, Sort.by("nombre").ascending()));
    }

    @Test
    public void test_list_dishes_empty_result() {
        // Arrange
        String restauranteNit = "123456789";
        Boolean activo = true;
        Long categoriaId = null;
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "nombre", true);

        Page<DishEntity> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10, Sort.by("nombre").ascending()), 0);

        when(dishRepository.findByRestauranteNitAndActivo(eq(restauranteNit), eq(activo), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        com.example.food_court_ms_small_square.domain.model.Page<Dish> result = dishJpaAdapter.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);

        // Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalPages());
        assertEquals(0, result.getTotalElements());
        verify(dishRepository).findByRestauranteNitAndActivo(restauranteNit, activo, PageRequest.of(0, 10, Sort.by("nombre").ascending()));
    }

    @Test
    public void test_list_dishes_with_category_filter() {
        // Arrange
        String restauranteNit = "123456789";
        Boolean activo = true;
        Long categoriaId = 1L;
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "nombre", true);

        DishEntity dish1 = new DishEntity(1L, "Dish 1", "Description 1", 10.0f, restauranteNit, "url1", true, null);
        DishEntity dish2 = new DishEntity(2L, "Dish 2", "Description 2", 20.0f, restauranteNit, "url2", true, null);
        List<DishEntity> dishEntities = Arrays.asList(dish1, dish2);

        Page<DishEntity> dishEntityPage = new PageImpl<>(dishEntities, PageRequest.of(0, 10, Sort.by("nombre").ascending()), 2);

        when(dishRepository.findByRestauranteNitAndActivoAndCategoriaId(eq(restauranteNit), eq(activo), eq(categoriaId), any(Pageable.class)))
                .thenReturn(dishEntityPage);

        when(dishEntityMapper.toDish(dish1)).thenReturn(new Dish(1L, "Dish 1", null, "Description 1", 10.0f, restauranteNit, "url1", true));
        when(dishEntityMapper.toDish(dish2)).thenReturn(new Dish(2L, "Dish 2", null, "Description 2", 20.0f, restauranteNit, "url2", true));

        // Act
        com.example.food_court_ms_small_square.domain.model.Page<Dish> result = dishJpaAdapter.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        verify(dishRepository).findByRestauranteNitAndActivoAndCategoriaId(restauranteNit, activo, categoriaId, PageRequest.of(0, 10, Sort.by("nombre").ascending()));
    }

    @Test
    public void test_sort_dishes_descending_by_specified_field() {
        // Arrange
        String restauranteNit = "123456789";
        Boolean activo = true;
        Long categoriaId = null;
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "precio", false);

        DishEntity dish1 = new DishEntity(1L, "Dish 1", "Description 1", 10.0f, restauranteNit, "url1", true, null);
        DishEntity dish2 = new DishEntity(2L, "Dish 2", "Description 2", 20.0f, restauranteNit, "url2", true, null);
        List<DishEntity> dishEntities = Arrays.asList(dish2, dish1);

        Page<DishEntity> dishEntityPage = new PageImpl<>(dishEntities, PageRequest.of(0, 10, Sort.by("precio").descending()), 2);

        when(dishRepository.findByRestauranteNitAndActivo(eq(restauranteNit), eq(activo), any(Pageable.class)))
                .thenReturn(dishEntityPage);

        when(dishEntityMapper.toDish(dish1)).thenReturn(new Dish(1L, "Dish 1", null, "Description 1", 10.0f, restauranteNit, "url1", true));
        when(dishEntityMapper.toDish(dish2)).thenReturn(new Dish(2L, "Dish 2", null, "Description 2", 20.0f, restauranteNit, "url2", true));

        // Act
        com.example.food_court_ms_small_square.domain.model.Page<Dish> result = dishJpaAdapter.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        assertEquals("Dish 2", result.getContent().get(0).getNombre());
        assertEquals("Dish 1", result.getContent().get(1).getNombre());
        verify(dishRepository).findByRestauranteNitAndActivo(restauranteNit, activo, PageRequest.of(0, 10, Sort.by("precio").descending()));
    }

    @Test
    public void test_sort_dishes_by_specified_field_ascending() {
        // Arrange
        String restauranteNit = "123456789";
        Boolean activo = true;
        Long categoriaId = null;
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "precio", true);

        DishEntity dish1 = new DishEntity(1L, "Dish 1", "Description 1", 10.0f, restauranteNit, "url1", true, null);
        DishEntity dish2 = new DishEntity(2L, "Dish 2", "Description 2", 20.0f, restauranteNit, "url2", true, null);
        List<DishEntity> dishEntities = Arrays.asList(dish1, dish2);

        Page<DishEntity> dishEntityPage = new PageImpl<>(dishEntities, PageRequest.of(0, 10, Sort.by("precio").ascending()), 2);

        when(dishRepository.findByRestauranteNitAndActivo(eq(restauranteNit), eq(activo), any(Pageable.class)))
                .thenReturn(dishEntityPage);

        when(dishEntityMapper.toDish(dish1)).thenReturn(new Dish(1L, "Dish 1", null, "Description 1", 10.0f, restauranteNit, "url1", true));
        when(dishEntityMapper.toDish(dish2)).thenReturn(new Dish(2L, "Dish 2", null, "Description 2", 20.0f, restauranteNit, "url2", true));

        // Act
        com.example.food_court_ms_small_square.domain.model.Page<Dish> result = dishJpaAdapter.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        verify(dishRepository).findByRestauranteNitAndActivo(restauranteNit, activo, PageRequest.of(0, 10, Sort.by("precio").ascending()));
    }
}
