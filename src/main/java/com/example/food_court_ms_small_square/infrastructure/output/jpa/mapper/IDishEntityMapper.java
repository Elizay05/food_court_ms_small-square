package com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper;

import com.example.food_court_ms_small_square.domain.model.Dish;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.DishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishEntityMapper {

    DishEntity toEntity(Dish dish);

    Dish toDish(DishEntity dishEntity);
}