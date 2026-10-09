package com.app.restaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {

    private Long id;
    private Integer userId;
    private Integer positionId;
    private Integer workModelId;
    private BigDecimal salary;
    private BigDecimal workingHours;
    private OffsetDateTime createdAt;
}
