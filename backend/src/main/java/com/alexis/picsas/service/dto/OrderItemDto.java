package com.alexis.picsas.service.dto;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderItemDto {
    private int pizzaId;
    @Min(value = 1 , message = "La cantidad no puede ser 0 o negativo")
    private int quantity;
}
