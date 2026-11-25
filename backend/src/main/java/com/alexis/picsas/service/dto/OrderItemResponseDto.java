package com.alexis.picsas.service.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderItemResponseDto {
    private Integer idItem;

    private Integer idOrder;

    private Integer idPizza;

    private Integer quantity;

    private BigDecimal price;
}
