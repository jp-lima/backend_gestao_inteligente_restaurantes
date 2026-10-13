package com.app.restaurante.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbsencesCreateRequestDTO {

    @NotNull(message = "O id do funcionário é obrigatório")
    private Long employeeId;

    @NotBlank(message = "O motivo é obrigatório")
    private String reason;

    @NotBlank(message = "A descrição é obrigatória")
    private String description;

    @NotNull(message = "A data/hora de início é obrigatória")
    private OffsetDateTime startAt;

    @NotNull(message = "A data/hora de término é obrigatória")
    private OffsetDateTime endAt;
}