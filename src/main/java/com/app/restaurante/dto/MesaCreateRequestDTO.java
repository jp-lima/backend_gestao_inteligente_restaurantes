package com.app.restaurante.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MesaCreateRequestDTO {

    @NotNull(message = "O número da mesa é obrigatório")
    @Positive(message = "O número da mesa deve ser positivo")
    private Integer numero;

    @NotNull(message = "A capacidade é obrigatória")
    @Min(value = 1, message = "A capacidade deve ser no mínimo 1")
    private Integer capacidade;

    @NotBlank(message = "O status é obrigatório")
    private String status;
}
