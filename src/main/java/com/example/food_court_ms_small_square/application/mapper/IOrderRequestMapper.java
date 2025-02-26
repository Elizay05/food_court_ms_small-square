package com.example.food_court_ms_small_square.application.mapper;

import com.example.food_court_ms_small_square.application.dto.request.OrderRequestDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderDishResponseDto;
import com.example.food_court_ms_small_square.application.dto.response.OrderResponseDto;
import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.domain.model.OrderDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderRequestMapper {
    Order toDomain(OrderRequestDto orderRequestDto);

    OrderResponseDto toResponseDto(Order order);

    @Mapping(source = "idPlato", target = "id")
    OrderDishResponseDto toResponseDto(OrderDish orderDish);

    List<OrderDishResponseDto> toResponseDtoList(List<OrderDish> platos);
}
