package com.example.food_court_ms_small_square.infrastructure.output.jpa.mapper;

import com.example.food_court_ms_small_square.domain.model.OrderDish;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderDishEntity;
import com.example.food_court_ms_small_square.infrastructure.output.jpa.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderDishEntityMapper {
    @Mapping(target = "id.idOrden", source = "ordenEntity.id")
    @Mapping(target = "id.idPlato", source = "ordenPlato.idPlato")
    @Mapping(target = "orden", source = "ordenEntity")
    @Mapping(target = "plato.id", source = "ordenPlato.idPlato")
    OrderDishEntity toEntity(OrderDish ordenPlato, OrderEntity ordenEntity);

    @Mapping(source = "id.idPlato", target = "idPlato")
    OrderDish toDomain(OrderDishEntity orderDishEntity);

    List<OrderDish> toDomainList(List<OrderDishEntity> orderDishEntities);
}
