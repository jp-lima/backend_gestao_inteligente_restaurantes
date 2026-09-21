package com.app.restaurante.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MesaResponseDTO {

    private UUID id;
    private Integer numero;
    private Integer capacidade;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public MesaResponseDTO() {
    }
}
