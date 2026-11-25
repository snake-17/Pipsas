package com.alexis.picsas.persistence.mapper;

import com.alexis.picsas.persistence.entity.OrderItemEntity;
import com.alexis.picsas.service.dto.OrderItemResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponseDto toDto(OrderItemEntity orderItemEntity);
}
