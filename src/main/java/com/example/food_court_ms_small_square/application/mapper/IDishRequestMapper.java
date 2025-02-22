package com.example.food_court_ms_small_square.application.mapper;

import com.example.food_court_ms_small_square.application.dto.request.DishRequestDto;
import com.example.food_court_ms_small_square.domain.model.Category;
import com.example.food_court_ms_small_square.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishRequestMapper {
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoryFromId")
    Dish toDish(DishRequestDto dishRequestDto);

    @Named("categoryFromId")
    default Category categoryFromId(Integer id) {
        if (id == null) {
            return null;
        }
        return new Category(Long.valueOf(id), null, null);
    }
}