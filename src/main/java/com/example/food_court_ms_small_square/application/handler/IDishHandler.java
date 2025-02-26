package com.example.food_court_ms_small_square.application.handler;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.DishResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.PageResponseDto;

public interface IDishHandler {
    void saveDish(DishRequestDto dishRequestDto);
    void updateDish(UpdateDishRequestDto updateDishRequestDto);
    void updateDishStatus (Long id, UpdateDishStatusRequestDto updateDishStatusRequestDto);
    PageResponseDto<DishResponseDto> listDishesByFilters(String restauranteNit, Boolean activo, Long categoriaId, int page, int size);
}
