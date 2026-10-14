package com.app.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionCreateRequestDTO {

    @NotBlank(message = "O tipo é obrigatório")
    private String type;
}
