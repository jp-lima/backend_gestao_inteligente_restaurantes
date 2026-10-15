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
        dto.setNumero(10);
        dto.setCapacidade(4);
        dto.setStatus("LIVRE");
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

        assertEquals(Set.of("numero", "capacidade", "status"), camposComErro);
    }

    @Test
    @DisplayName("deve reportar vários campos inválidos ao mesmo tempo")
    void variosCamposInvalidos() {
        MesaCreateRequestDTO dto = dtoValido();
        dto.setNumero(-1);
        dto.setCapacidade(0);
        dto.setStatus("");

        assertEquals(3, validar(dto).size());
    }

    // =====================================================
    // numero
    // =====================================================
    @Nested
    @DisplayName("numero")
    class Numero {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setNumero(null);

            assertViolacaoUnica(dto, "numero", "O número da mesa é obrigatório");
        }

        @ParameterizedTest(name = "numero = {0}")
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("zero ou negativo deve gerar violação")
        void naoPositivo(int valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setNumero(valor);

            assertViolacaoUnica(dto, "numero", "O número da mesa deve ser positivo");
        }

        @ParameterizedTest(name = "numero = {0}")
        @ValueSource(ints = {1, 5, 999})
        @DisplayName("positivo deve ser válido")
        void positivo(int valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setNumero(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // capacidade
    // =====================================================
    @Nested
    @DisplayName("capacidade")
    class Capacidade {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setCapacidade(null);

            assertViolacaoUnica(dto, "capacidade", "A capacidade é obrigatória");
        }

        @ParameterizedTest(name = "capacidade = {0}")
        @ValueSource(ints = {0, -1, -5})
        @DisplayName("zero ou negativo deve gerar violação")
        void menorQueUm(int valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setCapacidade(valor);

            assertViolacaoUnica(dto, "capacidade", "A capacidade deve ser no mínimo 1");
        }

        @ParameterizedTest(name = "capacidade = {0}")
        @ValueSource(ints = {1, 2, 4, 100})
        @DisplayName("um ou maior deve ser válido")
        void valido(int valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setCapacidade(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // status
    // =====================================================
    @Nested
    @DisplayName("status")
    class Status {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setStatus(null);

            assertViolacaoUnica(dto, "status", "O status é obrigatório");
        }

        @ParameterizedTest(name = "status = \"{0}\"")
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("vazio ou só com espaços/tab/newline deve gerar violação")
        void emBranco(String valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setStatus(valor);

            assertViolacaoUnica(dto, "status", "O status é obrigatório");
        }

        @ParameterizedTest(name = "status = \"{0}\"")
        @ValueSource(strings = {"LIVRE", "OCUPADA", "RESERVADA", "A"})
        @DisplayName("texto não vazio deve ser válido")
        void valido(String valor) {
            MesaCreateRequestDTO dto = dtoValido();
            dto.setStatus(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }
}
