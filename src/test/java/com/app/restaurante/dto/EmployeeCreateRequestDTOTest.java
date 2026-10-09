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

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa as anotações de validação do DTO chamando o Validator direto
 * (sem Spring, sem Mockito).
 */
class EmployeeCreateRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ---------- helpers ----------

    private EmployeeCreateRequestDTO dtoValido() {
        return new EmployeeCreateRequestDTO(
                10, 2, 3, new BigDecimal("3500.50"), new BigDecimal("40.00"));
    }

    private Set<ConstraintViolation<EmployeeCreateRequestDTO>> validar(EmployeeCreateRequestDTO dto) {
        return validator.validate(dto);
    }

    /** Confere que existe exatamente 1 violação, no campo e com a mensagem esperados. */
    private void assertViolacaoUnica(EmployeeCreateRequestDTO dto, String campo, String mensagem) {
        Set<ConstraintViolation<EmployeeCreateRequestDTO>> violacoes = validar(dto);

        assertEquals(1, violacoes.size(), "esperava exatamente 1 violação, vieram: " + violacoes);
        ConstraintViolation<EmployeeCreateRequestDTO> v = violacoes.iterator().next();
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
        Set<String> camposComErro = validar(new EmployeeCreateRequestDTO()).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(
                Set.of("userId", "positionId", "workModelId", "salary", "workingHours"),
                camposComErro);
    }

    @Test
    @DisplayName("deve reportar vários campos inválidos ao mesmo tempo")
    void variosCamposInvalidos() {
        EmployeeCreateRequestDTO dto = dtoValido();
        dto.setSalary(new BigDecimal("-1"));
        dto.setWorkingHours(BigDecimal.ZERO);

        assertEquals(2, validar(dto).size());
    }

    // =====================================================
    // userId, positionId, workModelId
    // =====================================================
    @Nested
    @DisplayName("ids (userId, positionId, workModelId)")
    class Ids {

        @Test
        @DisplayName("userId nulo deve gerar violação")
        void userIdNulo() {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setUserId(null);

            assertViolacaoUnica(dto, "userId", "User ID is required");
        }

        @Test
        @DisplayName("positionId nulo deve gerar violação")
        void positionIdNulo() {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setPositionId(null);

            assertViolacaoUnica(dto, "positionId", "Position ID is required");
        }

        @Test
        @DisplayName("workModelId nulo deve gerar violação")
        void workModelIdNulo() {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setWorkModelId(null);

            assertViolacaoUnica(dto, "workModelId", "Work model ID is required");
        }

        /*
         * COMPORTAMENTO ATUAL: os ids só têm @NotNull, sem @Positive.
         * Zero e negativos passam na validação. Se a equipe quiser barrar, adicionar @Positive
         * nos três campos e trocar este teste por um que espere violação.
         */
        @ParameterizedTest(name = "id = {0}")
        @ValueSource(ints = {0, -1})
        @DisplayName("hoje ids zero ou negativos passam na validação (só há @NotNull)")
        void idsNaoPositivos_hojePassam(int valor) {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setUserId(valor);
            dto.setPositionId(valor);
            dto.setWorkModelId(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // salary
    // =====================================================
    @Nested
    @DisplayName("salary")
    class Salary {

        @Test
        @DisplayName("salary nulo deve gerar uma única violação (@NotNull), sem duplicar com @DecimalMin")
        void salaryNulo() {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setSalary(null);

            assertViolacaoUnica(dto, "salary", "Salary is required");
        }

        @ParameterizedTest(name = "salary = {0}")
        @ValueSource(strings = {"-0.01", "-1", "-1000.50"})
        @DisplayName("salary negativo deve gerar violação")
        void salaryNegativo(String valor) {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setSalary(new BigDecimal(valor));

            assertViolacaoUnica(dto, "salary", "Salary cannot be negative");
        }

        @ParameterizedTest(name = "salary = {0}")
        @ValueSource(strings = {"0", "0.0", "0.00", "0.01", "1", "3500.50", "9999999999.99"})
        @DisplayName("salary zero (limite inclusivo) e positivos devem ser válidos")
        void salaryValido(String valor) {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setSalary(new BigDecimal(valor));

            assertTrue(validar(dto).isEmpty());
        }

        /*
         * ATENÇÃO: a coluna é precision = 12, scale = 2 (máximo 9.999.999.999,99), mas o DTO não
         * tem @Digits nem @DecimalMax. Um salário maior que isso passa na validação do DTO e
         * só falha no banco. Sugestão: @Digits(integer = 10, fraction = 2) no campo.
         */
        @Test
        @DisplayName("hoje salary acima do limite da coluna passa na validação do DTO (sem @Digits)")
        void salaryAcimaDaColuna_hojePassa() {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setSalary(new BigDecimal("99999999999999.99"));

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // workingHours
    // =====================================================
    @Nested
    @DisplayName("workingHours")
    class WorkingHours {

        @Test
        @DisplayName("workingHours nulo deve gerar uma única violação (@NotNull), sem duplicar com @DecimalMin")
        void workingHoursNulo() {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setWorkingHours(null);

            assertViolacaoUnica(dto, "workingHours", "Working hours are required");
        }

        @ParameterizedTest(name = "workingHours = {0}")
        @ValueSource(strings = {"0", "0.0", "0.00"})
        @DisplayName("workingHours zero deve gerar violação (limite exclusivo)")
        void workingHoursZero(String valor) {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setWorkingHours(new BigDecimal(valor));

            assertViolacaoUnica(dto, "workingHours", "Working hours must be greater than zero");
        }

        @ParameterizedTest(name = "workingHours = {0}")
        @ValueSource(strings = {"-0.01", "-1", "-40"})
        @DisplayName("workingHours negativo deve gerar violação")
        void workingHoursNegativo(String valor) {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setWorkingHours(new BigDecimal(valor));

            assertViolacaoUnica(dto, "workingHours", "Working hours must be greater than zero");
        }

        @ParameterizedTest(name = "workingHours = {0}")
        @ValueSource(strings = {"0.01", "1", "6", "37.5", "40", "44.00", "999.99"})
        @DisplayName("workingHours positivo deve ser válido")
        void workingHoursValido(String valor) {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setWorkingHours(new BigDecimal(valor));

            assertTrue(validar(dto).isEmpty());
        }

        /*
         * ATENÇÃO: não há limite máximo. A coluna é precision = 5, scale = 2 (máximo 999,99),
         * então 1000 passa no DTO e só falha no banco; e valores como 25 passam mesmo
         * sendo mais que um dia. Se houver regra de negócio (ex.: até 168 por semana),
         * usar @DecimalMax e/ou @Digits(integer = 3, fraction = 2).
         */
        @ParameterizedTest(name = "workingHours = {0}")
        @ValueSource(strings = {"25", "168.01", "1000"})
        @DisplayName("hoje workingHours muito altos (ou acima da coluna) passam na validação (sem @DecimalMax/@Digits)")
        void workingHoursAltos_hojePassam(String valor) {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setWorkingHours(new BigDecimal(valor));

            assertTrue(validar(dto).isEmpty());
        }
    }
}