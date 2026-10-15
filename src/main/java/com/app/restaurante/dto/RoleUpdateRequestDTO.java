package com.app.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleUpdateRequestDTO {

    @NotBlank(message = "A role é obrigatória")
    @Pattern(regexp = "(?i)ADMIN|USER", message = "Role deve ser ADMIN ou USER")
    private String role;
}
