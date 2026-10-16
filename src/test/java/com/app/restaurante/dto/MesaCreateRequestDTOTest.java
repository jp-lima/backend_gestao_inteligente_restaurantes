package com.app.restaurante.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa as anotações de validação do {@link MesaCreateRequestDTO} chamando o Validator direto
 * (sem Spring, sem Mockito).
 */
class MesaCreateRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ---------- helpers ----------

    private MesaCreateRequestDTO dtoValido() {
        MesaCreateRequestDTO dto = new MesaCreateRequestDTO();
        dto.setNumber(10);
        dto.setSeating_capacity(4);
        dto.setAvailable(true);
        return dto;
    }

    private Set<ConstraintViolation<MesaCreateRequestDTO>> validar(MesaCreateRequestDTO dto) {
        return validator.validate(dto);
    }

    private void assertViolacaoUnica(MesaCreateRequestDTO dto, String campo, String mensagem) {
        Set<ConstraintViolation<MesaCreateRequestDTO>> violacoes = validar(dto);

        assertEquals(1, violacoes.size(), "esperava exatamente 1 violação, vieram: " + violacoes);
        ConstraintViolation<MesaCreateRequestDTO> v = violacoes.iterator().next();
        assertEquals(campo, v.getPropertyPath().toString());
        assertEquals(mensagem, v.getMessage());
    }

    // =====================================================
    // DTO como um todo
    // =====================================================
    @Test
    @DisplayName("DTO válido não deve ter violações")
    void dtoValido_semViolacoes() {
        assertTrue(validar(dtoValido()).isEmpty());
    }

    @Test
    @DisplayName("DTO vazio (como o Jackson cria antes de preencher) deve violar os 3 campos")
    void dtoVazio_violaTodosOsCampos() {
        Set<String> camposComErro = validar(new MesaCreateRequestDTO()).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of("number", "seating_capacity", "available"), camposComErro);
    }

    @Test
    @DisplayName("deve reportar vários campos inválidos ao mesmo tempo")
    void variosCamposInvalidos() {
        MesaCreateRequestDTO dto = dtoValido();
        dto.setNumber(-1);
        dto.setSeating_capacity(0);
        dto.setAvailable(null);

        assertEquals(3, validar(dto).size());
    }

    // =====================================================
    // number
    // =====================================================
    @Nested
    @DisplayName("number")
    class Number {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setNumber(null);

            assertViolacaoUnica(dto, "number", "O número da mesa é obrigatório");
        }

        @ParameterizedTest(name = "number = {0}")
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("zero ou negativo deve gerar violação")
        void naoPositivo(int valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setNumber(valor);

            assertViolacaoUnica(dto, "number", "O número da mesa deve ser positivo");
        }

        @ParameterizedTest(name = "number = {0}")
        @ValueSource(ints = {1, 5, 999})
        @DisplayName("positivo deve ser válido")
        void positivo(int valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setNumber(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // seating_capacity
    // =====================================================
    @Nested
    @DisplayName("seating_capacity")
    class SeatingCapacity {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setSeating_capacity(null);

            assertViolacaoUnica(dto, "seating_capacity", "A capacidade é obrigatória");
        }

        @ParameterizedTest(name = "seating_capacity = {0}")
        @ValueSource(ints = {0, -1, -5})
        @DisplayName("zero ou negativo deve gerar violação")
        void menorQueUm(int valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setSeating_capacity(valor);

            assertViolacaoUnica(dto, "seating_capacity", "A capacidade deve ser no mínimo 1");
        }

        @ParameterizedTest(name = "seating_capacity = {0}")
        @ValueSource(ints = {1, 2, 4, 100})
        @DisplayName("um ou maior deve ser válido")
        void valido(int valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setSeating_capacity(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // available
    // =====================================================
    @Nested
    @DisplayName("available")
    class Available {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setAvailable(null);

            assertViolacaoUnica(dto, "available", "O campo available é obrigatório");
        }

        @ParameterizedTest(name = "available = {0}")
        @ValueSource(booleans = {true, false})
        @DisplayName("true ou false deve ser válido")
        void valido(boolean valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setAvailable(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }
}
