package com.app.restaurante.dto;

import lombok.AllArgsConstructor;
import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SuppliesResponseDTO {
    private Long id;
    private String name;
    private Integer quantity;
    private Integer minimumStock;
    private String unit;
    private Boolean perishable;
    private LocalDate expiration_date;
    private OffsetDateTime updatedAt;
}