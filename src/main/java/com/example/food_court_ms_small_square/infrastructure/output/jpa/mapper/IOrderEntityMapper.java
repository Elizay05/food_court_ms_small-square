package com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper;

import com.example.food_court_ms_small_square.domain.model.Order;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {
    OrderEntity toEntity(Order order);
    Order toDomain(OrderEntity orderEntity);
}
