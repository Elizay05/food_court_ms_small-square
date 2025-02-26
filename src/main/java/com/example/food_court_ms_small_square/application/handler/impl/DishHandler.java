package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.DishResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;
import com.example.food_court_ms_small_square.application.handler.IDishHandler;
import com.example.food_court_ms_small_square.application.mapper.IDishRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.model.Page;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;

    @Override
    public void saveDish(DishRequestDto dishRequestDto) {
        Dish dish = dishRequestMapper.toDish(dishRequestDto);
        dishServicePort.saveDish(dish);
    }

    @Override
    public void updateDish(UpdateDishRequestDto updateDishRequestDto) {
        dishServicePort.updateDish(
                updateDishRequestDto.getId().longValue(),
                updateDishRequestDto.getPrice(),
                updateDishRequestDto.getDescription()
        );
    }

    @Override
    public void updateDishStatus(Long id, UpdateDishStatusRequestDto updateDishStatusRequestDto) {
        dishServicePort.updateDishStatus(
                id,
                updateDishStatusRequestDto.getEnabled()
        );
    }

    @Override
    public PageResponseDto<DishResponseDto> listDishesByFilters(String restauranteNit, Boolean activo, Long categoriaId, int page, int size) {
        PageRequestDto pageRequestDto = new PageRequestDto(page, size, "nombre", true);
        Page<Dish> dishPage = dishServicePort.listDishesByFilters(restauranteNit, activo, categoriaId, pageRequestDto);

        List<DishResponseDto> dishDtos = dishPage.getContent().stream()
                .map(dishRequestMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(dishDtos, dishPage.getTotalPages(), dishPage.getTotalElements());
    }

}
