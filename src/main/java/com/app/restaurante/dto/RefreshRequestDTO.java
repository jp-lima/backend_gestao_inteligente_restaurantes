package com.app.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshRequestDTO {

    @NotBlank(message = "O refresh token é obrigatório")
    private String refreshToken;
}
