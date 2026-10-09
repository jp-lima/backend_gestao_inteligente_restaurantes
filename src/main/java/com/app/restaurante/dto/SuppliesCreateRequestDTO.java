package com.app.restaurante.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuppliesCreateRequestDTO {

    @NotBlank(message = "O nome do insumo é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String name;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantity;

    @NotNull(message = "O estoque mínimo é obrigatório")
    @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
    private Integer minimumStock;

    @NotBlank(message = "A unidade de medida é obrigatória")
    private String unit; // Ex: "KG", "L", "UN", "PCT"

    @NotNull(message = "Informe se o insumo é perecível ou não")
    private Boolean perishable;

    @Future(message = "A data de expiração deve ser uma data futura")
    private LocalDate expiration_date;
}