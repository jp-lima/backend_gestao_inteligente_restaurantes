package com.app.restaurante.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa as anotações de validação do DTO chamando o Validator direto
 * (sem Spring, sem Mockito).
 */
class CategoriesCreateRequestDTOTest {

    private static final String MENSAGEM_TYPE_EM_BRANCO =
            "O tipo da categoria não pode ser deixado em branco";

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("DTO válido não deve ter violações")
    void dtoValido_semViolacoes() {
        CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO("Bebidas");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @ParameterizedTest(name = "type = [{0}] deve ser válido")
    @ValueSource(strings = {"A", "Bebidas", "Sobremesas e doces", "Açaí & Cia", " Bebidas "})
    @DisplayName("types com conteúdo (inclusive 1 caractere, acentos e espaços nas pontas) são válidos")
    void typeComConteudo_valido(String valor) {
        CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO(valor);

        assertTrue(validator.validate(dto).isEmpty());
    }

    @ParameterizedTest(name = "type = [{0}] deve ser inválido")
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    @DisplayName("type nulo, vazio ou só com espaços deve ser inválido")
    void typeNuloOuEmBranco_invalido(String valor) {
        CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO(valor);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("type vazio deve gerar exatamente uma violação, no campo type")
    void typeVazio_umaViolacaoNoCampoCerto() {
        CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO("");

        Set<ConstraintViolation<CategoriesCreateRequestDTO>> violacoes = validator.validate(dto);

        assertEquals(1, violacoes.size());
        assertEquals("type", violacoes.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("violação deve trazer a mensagem de erro configurada")
    void typeVazio_mensagemCorreta() {
        CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO(" ");

        ConstraintViolation<CategoriesCreateRequestDTO> violacao =
                validator.validate(dto).iterator().next();

        assertEquals(MENSAGEM_TYPE_EM_BRANCO, violacao.getMessage());
    }

    @Test
    @DisplayName("DTO criado pelo construtor vazio (como o Jackson faz) é inválido até preencher o type")
    void construtorVazio_invalidoAteSetarType() {
        CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO();

        assertFalse(validator.validate(dto).isEmpty());

        dto.setType("Bebidas");

        assertTrue(validator.validate(dto).isEmpty());
    }
}