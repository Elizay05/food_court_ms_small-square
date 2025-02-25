package com.example.food_court_ms_small_square.application.handler.impl;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.DishResponseDto;
import com.example.food_court_ms_small_square.application.handler.IDishHandler;
import com.example.food_court_ms_small_square.application.mapper.IDishRequestMapper;
import com.example.food_court_ms_small_square.domain.api.IDishServicePort;
import com.example.food_court_ms_small_square.domain.model.Dish;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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
    public Page<DishResponseDto> listDishes(int page, int size) {
        return dishServicePort.listDishes(page, size)
                .map(dishRequestMapper::toResponseDto);
    }
}
