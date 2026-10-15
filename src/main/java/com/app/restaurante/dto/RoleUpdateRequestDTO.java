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
    @Pattern(regexp = "(?i)ADMIN|USER|WAITER", message = "Role deve ser ADMIN, USER ou WAITER")
    private String role;
}
