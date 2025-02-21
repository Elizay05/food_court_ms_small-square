package com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper;

import com.example.food_court_ms_small_square.domain.model.Restaurant;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantEntityMapper {
    RestaurantEntity toEntity(Restaurant restaurant);
    Restaurant toModel(RestaurantEntity restaurantEntity);
}
