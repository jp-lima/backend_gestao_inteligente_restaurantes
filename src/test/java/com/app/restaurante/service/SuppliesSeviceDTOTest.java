package com.app.restaurante.service;

import com.app.restaurante.dto.SuppliesCreateRequestDTO;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeAll;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SuppliesSeviceDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private SuppliesCreateRequestDTO dtoValido() {
      return new SuppliesCreateRequestDTO("Leite", 80, 5, "L", false, LocalDate.now().plusDays(30));
    }

    @Test
    @DisplayName("DTO deve da valido")
    void dtoValido_DeveOcorrerNormal(){
        assertTrue(validator.validate(dtoValido()).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Deve aparecer mensagem de erro por nome invalido")
    void nomeInvalido(String valor){
        SuppliesCreateRequestDTO dto = dtoValido();
        dto.setName(valor);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("quantity negativa deve gerar violacao")
    void quantidadeNegativa_gerarViolacao(){
        SuppliesCreateRequestDTO dto = dtoValido();
        dto.setQuantity(-1);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Data resultar invalido por nao ser futuro")
    void dataAtual_gerarViolacao(){
        SuppliesCreateRequestDTO dto = dtoValido();
        dto.setExpiration_date(LocalDate.now());

        assertFalse(validator.validate(dto).isEmpty());
    }

}
