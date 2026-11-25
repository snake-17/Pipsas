package com.alexis.picsas.service.dto;

import com.alexis.picsas.persistence.entity.Method;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckoutDto {
    @NotNull(message = "El método de pago es obligatorio")
    private Method method;
    @Size(max = 200)
    private String additional_notes;
}
