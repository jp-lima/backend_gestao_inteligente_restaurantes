package com.app.restaurante.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MesaResponseDTO {

    private Integer id;
    private Integer number;
    private Integer seating_capacity;
    private Boolean available;
    private LocalDateTime created_at;

    public MesaResponseDTO() {
    }
}
