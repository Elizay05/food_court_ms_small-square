package com.example.food_court_ms_small_square.application.handler;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishRequestDto;
import com.example.food_court_ms_small_square.application.dto.request.UpdateDishStatusRequestDto;

public interface IDishHandler {
    void saveDish(DishRequestDto dishRequestDto);
    void updateDish(UpdateDishRequestDto updateDishRequestDto);
    void updateDishStatus (Long id, UpdateDishStatusRequestDto updateDishStatusRequestDto);
}
