package com.example.food_court_ms_small_square.application.handler;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;

public interface IDishHandler {
    void saveDish(DishRequestDto dishRequestDto);
}
