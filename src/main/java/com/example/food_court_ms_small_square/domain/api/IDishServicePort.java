package com.example.food_court_ms_small_square.domain.api;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.model.Page;

public interface IDishServicePort {
    void saveDish(Dish dish);
    void updateDish(Long id, Float price, String description);
    void updateDishStatus(Long id, Boolean enabled);
    Page<Dish> listDishesByFilters(String restauranteNit, Boolean activo, Long categoriaId, PageRequestDto pageRequestDto);
}
