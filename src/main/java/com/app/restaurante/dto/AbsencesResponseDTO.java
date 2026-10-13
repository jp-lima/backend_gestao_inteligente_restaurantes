package com.app.restaurante.dto;
import com.app.restaurante.entity.AbsenceStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbsencesResponseDTO {
    private Long id;
    private Long employeeId;
    private String reason;
    private String description;
    private AbsenceStatus status;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private OffsetDateTime createdAt;
}