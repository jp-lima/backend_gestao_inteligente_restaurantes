package com.app.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PratoCreateRequestDTO {

    @NotBlank(message = "O nome do prato é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal price;

    @NotBlank(message = "A categoria é obrigatória")
    private String category;

    @NotNull(message = "O ID da categoria é obrigatório")
    private Integer categoryId;

    private boolean available = true;

    private String imageUrl;
}
