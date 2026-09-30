package com.app.restaurante.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PratoResponseDTO {

    private Integer id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private Integer categoryId;
    private boolean disponivel;
    private String imagemUrl;
    private LocalDateTime createdAt;
}
