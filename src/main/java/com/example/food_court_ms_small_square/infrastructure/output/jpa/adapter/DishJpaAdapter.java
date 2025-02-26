package com.example.food_court_ms_small_square.infrastructure.output.jpa.adapter;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExistsException;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.model.Page;
import com.example.food_court_ms_small_square.domain.spi.IDishPersistencePort;
import com.example.food_court_ms_small_square.infrastructure.configuration.security.CustomUserDetails;
import com.example.food_court_ms_small_square.infrastructure.exception.NoSuchElementException;
import com.example.food_court_ms_small_square.infrastructure.exception.UnauthorizedException;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IDishRespository;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishEntityMapper dishEntityMapper;
    private final IDishRespository dishRepository;
    private final IRestaurantRepository restaurantRepository;

    @Override
    public void saveDish(Dish dish) {
        RestaurantEntity restaurantEntity = restaurantRepository.findById(dish.getRestauranteNit())
                .orElseThrow(() -> new NoSuchElementException("El restaurante no existe"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String documentNumber = userDetails.getDocumentNumber();

        if (!restaurantEntity.getCedulaPropietario().equals(documentNumber)) {
            throw new UnauthorizedException("No tienes permisos para crear un plato en este restaurante");
        }

        if (dishRepository.existsByNombreAndRestauranteNit(dish.getNombre(), dish.getRestauranteNit())) {
            throw new ElementAlreadyExistsException("El plato ya existe");
        }

        DishEntity dishEntity = dishEntityMapper.toEntity(dish);
        dishRepository.save(dishEntity);
    }

    @Override
    public Boolean dishExists(String name, String nit) {
        return dishRepository.existsByNombreAndRestauranteNit(name, nit);
    }

    @Override
    public void updateDish(Long id, Float precio, String descripcion) {
        DishEntity dishEntity = dishRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El plato no existe."));

        Optional<RestaurantEntity> restaurantEntity = restaurantRepository.findById(dishEntity.getRestauranteNit());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String documentNumber = userDetails.getDocumentNumber();

        if (!restaurantEntity.get().getCedulaPropietario().equals(documentNumber)) {
            throw new UnauthorizedException("No tienes permisos para actualizar un plato en este restaurante");
        }

        if (descripcion != null) {
            dishEntity.setDescripcion(descripcion);
        }
        if (precio != null) {
            dishEntity.setPrecio(precio);
        }

        dishRepository.save(dishEntity);
    }

    @Override
    public void updateDishStatus(Long id, Boolean enabled) {
        DishEntity dishEntity = dishRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El plato no existe."));

        Optional<RestaurantEntity> restaurantEntity = restaurantRepository.findById(dishEntity.getRestauranteNit());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String documentNumber = userDetails.getDocumentNumber();

        if (!restaurantEntity.get().getCedulaPropietario().equals(documentNumber)) {
            throw new UnauthorizedException("No tienes permisos para actualizar un plato en este restaurante");
        }

        dishEntity.setActivo(enabled);
        dishRepository.save(dishEntity);
    }

    @Override
    public Page<Dish> listDishesByFilters(String restauranteNit, Boolean activo, Long categoriaId, PageRequestDto pageRequestDto) {
        Sort sort = pageRequestDto.isAscending()
                ? Sort.by(pageRequestDto.getSortBy()).ascending()
                : Sort.by(pageRequestDto.getSortBy()).descending();

        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);

        org.springframework.data.domain.Page<DishEntity> dishPage;
        if (categoriaId == null) {
            dishPage = dishRepository.findByRestauranteNitAndActivo(restauranteNit, activo, pageable);
        } else {
            dishPage = dishRepository.findByRestauranteNitAndActivoAndCategoriaId(restauranteNit, activo, categoriaId, pageable);
        }

        List<Dish> dishes = dishPage.getContent().stream()
                .map(dishEntityMapper::toDish)
                .collect(Collectors.toList());

        return new Page<>(dishes, dishPage.getTotalPages(), dishPage.getTotalElements());
    }
}
