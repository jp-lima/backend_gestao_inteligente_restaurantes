package com.app.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CategoriesCreateRequestDTO {
    @NotBlank(message = "O tipo da categoria não pode ser deixado em branco")
    private  String type;
}
