package com.app.restaurante.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateRequestDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Position ID is required")
    private Integer positionId;

    @NotNull(message = "Work model ID is required")
    private Integer workModelId;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Salary cannot be negative")
    private BigDecimal salary;

    @NotNull(message = "Working hours are required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Working hours must be greater than zero")
    private BigDecimal workingHours;
}
