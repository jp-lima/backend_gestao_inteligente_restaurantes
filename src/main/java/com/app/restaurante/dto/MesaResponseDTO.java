package com.app.restaurante.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MesaResponseDTO {

    private Long id;
    private Integer numero;
    private Integer capacidade;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public MesaResponseDTO() {
    }
}
