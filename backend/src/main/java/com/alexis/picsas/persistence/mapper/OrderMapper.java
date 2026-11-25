package com.alexis.picsas.persistence.mapper;

import com.alexis.picsas.persistence.entity.OrderEntity;
import com.alexis.picsas.service.dto.OrderDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "idOrder", target = "orderId")
    OrderDto toDto(OrderEntity orderEntity);

}
