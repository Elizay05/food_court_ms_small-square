package com.example.food_court_ms_small_square.domain.spi;

import com.example.food_court_ms_small_square.application.dto.request.PageRequestDto;
import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.domain.model.Page;

public interface IDishPersistencePort {
    void saveDish(Dish dish);
    Boolean dishExists(String name, String nit);
    void updateDish(Long id, Float precio, String descripcion);
    void updateDishStatus(Long id, Boolean enabled);
    Page<Dish> listDishesByFilters(String restauranteNit, Boolean activo, Long categoriaId, PageRequestDto pageRequestDto);
}
