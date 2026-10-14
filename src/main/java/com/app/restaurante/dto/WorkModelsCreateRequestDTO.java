package com.app.restaurante.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkModelsCreateRequestDTO {

    @NotBlank(message = "O tipo é obrigatório")
    private String type;
}