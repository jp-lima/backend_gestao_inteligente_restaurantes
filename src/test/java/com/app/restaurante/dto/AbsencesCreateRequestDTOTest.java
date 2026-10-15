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

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa as anotações de validação do {@link AbsencesCreateRequestDTO} chamando o Validator direto
 * (sem Spring, sem Mockito).
 *
 * O campo {@code status} não existe no DTO de criação; o service define {@code PEDIDO} por padrão.
 */
class AbsencesCreateRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ---------- helpers ----------

    private AbsencesCreateRequestDTO dtoValido() {
        AbsencesCreateRequestDTO dto = new AbsencesCreateRequestDTO();
        dto.setEmployeeId(1L);
        dto.setReason("Férias");
        dto.setDescription("Viagem em família");
        dto.setStartAt(OffsetDateTime.parse("2026-12-01T08:00:00-03:00"));
        dto.setEndAt(OffsetDateTime.parse("2026-12-10T18:00:00-03:00"));
        return dto;
    }

    private Set<ConstraintViolation<AbsencesCreateRequestDTO>> validar(AbsencesCreateRequestDTO dto) {
        return validator.validate(dto);
    }

    private void assertViolacaoUnica(AbsencesCreateRequestDTO dto, String campo, String mensagem) {
        Set<ConstraintViolation<AbsencesCreateRequestDTO>> violacoes = validar(dto);

        assertEquals(1, violacoes.size(), "esperava exatamente 1 violação, vieram: " + violacoes);
        ConstraintViolation<AbsencesCreateRequestDTO> v = violacoes.iterator().next();
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
    @DisplayName("DTO vazio (como o Jackson cria antes de preencher) deve violar os 5 campos")
    void dtoVazio_violaTodosOsCampos() {
        Set<String> camposComErro = validar(new AbsencesCreateRequestDTO()).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of("employeeId", "reason", "description", "startAt", "endAt"), camposComErro);
    }

    @Test
    @DisplayName("deve reportar vários campos inválidos ao mesmo tempo")
    void variosCamposInvalidos() {
        AbsencesCreateRequestDTO dto = dtoValido();
        dto.setEmployeeId(null);
        dto.setReason("");
        dto.setStartAt(null);

        assertEquals(3, validar(dto).size());
    }

    // =====================================================
    // employeeId
    // =====================================================
    @Nested
    @DisplayName("employeeId")
    class EmployeeId {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setEmployeeId(null);

            assertViolacaoUnica(dto, "employeeId", "O id do funcionário é obrigatório");
        }

        /*
         * COMPORTAMENTO ATUAL: employeeId só tem @NotNull, sem @Positive.
         * Zero e negativos passam na validação. Se a equipe quiser barrar, adicionar @Positive.
         */
        @ParameterizedTest(name = "employeeId = {0}")
        @ValueSource(longs = {0L, -1L})
        @DisplayName("hoje zero ou negativo passam na validação (só há @NotNull)")
        void naoPositivo_hojePassa(long valor) {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setEmployeeId(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // reason
    // =====================================================
    @Nested
    @DisplayName("reason")
    class Reason {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setReason(null);

            assertViolacaoUnica(dto, "reason", "O motivo é obrigatório");
        }

        @ParameterizedTest(name = "reason = \"{0}\"")
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("vazio ou só com espaços/tab/newline deve gerar violação")
        void emBranco(String valor) {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setReason(valor);

            assertViolacaoUnica(dto, "reason", "O motivo é obrigatório");
        }

        @ParameterizedTest(name = "reason = \"{0}\"")
        @ValueSource(strings = {"Férias", "A", "Licença médica"})
        @DisplayName("texto não vazio deve ser válido")
        void valido(String valor) {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setReason(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // description
    // =====================================================
    @Nested
    @DisplayName("description")
    class Description {

        @Test
        @DisplayName("nulo deve gerar violação")
        void nulo() {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setDescription(null);

            assertViolacaoUnica(dto, "description", "A descrição é obrigatória");
        }

        @ParameterizedTest(name = "description = \"{0}\"")
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("vazio ou só com espaços/tab/newline deve gerar violação")
        void emBranco(String valor) {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setDescription(valor);

            assertViolacaoUnica(dto, "description", "A descrição é obrigatória");
        }

        @ParameterizedTest(name = "description = \"{0}\"")
        @ValueSource(strings = {"Viagem", "A", "Detalhes da ausência"})
        @DisplayName("texto não vazio deve ser válido")
        void valido(String valor) {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setDescription(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // startAt / endAt
    // =====================================================
    @Nested
    @DisplayName("datas")
    class Datas {

        @Test
        @DisplayName("startAt nulo deve gerar violação")
        void startAtNulo() {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setStartAt(null);

            assertViolacaoUnica(dto, "startAt", "A data/hora de início é obrigatória");
        }

        @Test
        @DisplayName("endAt nulo deve gerar violação")
        void endAtNulo() {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setEndAt(null);

            assertViolacaoUnica(dto, "endAt", "A data/hora de término é obrigatória");
        }

        /*
         * COMPORTAMENTO ATUAL: não há validação de ordenação entre startAt e endAt.
         * Uma ausência com início após o término passa na validação do DTO e do service.
         * Se a equipe quiser barrar, adicionar @Future/@Past ou validação manual no service.
         */
        @Test
        @DisplayName("hoje startAt depois de endAt passa na validação (sem regra de ordenação)")
        void datasInvertidas_hojePassam() {
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setStartAt(OffsetDateTime.parse("2026-12-10T18:00:00-03:00"));
            dto.setEndAt(OffsetDateTime.parse("2026-12-01T08:00:00-03:00"));

            assertTrue(validar(dto).isEmpty());
        }
    }
}
