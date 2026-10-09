package com.app.restaurante.mapper;

import com.app.restaurante.dto.EmployeeCreateRequestDTO;
import com.app.restaurante.dto.EmployeeResponseDTO;
import com.app.restaurante.entity.EmployeeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    private EmployeeMapper mapper;

    private static final OffsetDateTime CRIADO_EM = OffsetDateTime.parse("2026-10-01T10:30:00-03:00");

    @BeforeEach
    void setUp() {
        mapper = new EmployeeMapper();
    }

    // ---------- helpers ----------

    private EmployeeCreateRequestDTO dtoValido() {
        EmployeeCreateRequestDTO dto = new EmployeeCreateRequestDTO();
        dto.setUserId(10);
        dto.setPositionId(2);
        dto.setWorkModelId(3);
        dto.setSalary(new BigDecimal("3500.50"));
        dto.setWorkingHours(new BigDecimal("40.00"));
        return dto;
    }

    private EmployeeEntity entityCompleta() {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(1L);
        entity.setUserId(10);
        entity.setPositionId(2);
        entity.setWorkModelId(3);
        entity.setSalary(new BigDecimal("3500.50"));
        entity.setWorkingHours(new BigDecimal("40.00"));
        entity.setCreatedAt(CRIADO_EM);
        return entity;
    }

    // =====================================================
    // toEntity
    // =====================================================
    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("deve copiar todos os campos do DTO para a entity")
        void copiaTodosOsCampos() {
            EmployeeEntity entity = mapper.toEntity(dtoValido());

            assertNotNull(entity);
            assertEquals(10, entity.getUserId());
            assertEquals(2, entity.getPositionId());
            assertEquals(3, entity.getWorkModelId());
            assertEquals(new BigDecimal("3500.50"), entity.getSalary());
            assertEquals(new BigDecimal("40.00"), entity.getWorkingHours());
        }

        @Test
        @DisplayName("não deve preencher id nem createdAt (são gerados pelo banco/Hibernate)")
        void naoPreencheCamposGerados() {
            EmployeeEntity entity = mapper.toEntity(dtoValido());

            assertNull(entity.getId());
            assertNull(entity.getCreatedAt());
        }

        @Test
        @DisplayName("deve preservar a escala do salário e das horas (sem arredondar)")
        void preservaEscalaDosDecimais() {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setSalary(new BigDecimal("1234.5"));
            dto.setWorkingHours(new BigDecimal("37.5"));

            EmployeeEntity entity = mapper.toEntity(dto);

            assertEquals(new BigDecimal("1234.5"), entity.getSalary());
            assertEquals(new BigDecimal("37.5"), entity.getWorkingHours());
        }

        @Test
        @DisplayName("deve aceitar valores de borda (salário e horas zerados)")
        void aceitaValoresZerados() {
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setSalary(BigDecimal.ZERO);
            dto.setWorkingHours(BigDecimal.ZERO);

            EmployeeEntity entity = mapper.toEntity(dto);

            assertEquals(0, BigDecimal.ZERO.compareTo(entity.getSalary()));
            assertEquals(0, BigDecimal.ZERO.compareTo(entity.getWorkingHours()));
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            EmployeeCreateRequestDTO dto = dtoValido();

            assertNotSame(mapper.toEntity(dto), mapper.toEntity(dto));
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            EmployeeCreateRequestDTO dto = dtoValido();

            mapper.toEntity(dto);

            assertEquals(10, dto.getUserId());
            assertEquals(2, dto.getPositionId());
            assertEquals(3, dto.getWorkModelId());
            assertEquals(new BigDecimal("3500.50"), dto.getSalary());
            assertEquals(new BigDecimal("40.00"), dto.getWorkingHours());
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando o DTO for nulo")
        void dtoNuloLancaNPE() {
            assertThrows(NullPointerException.class, () -> mapper.toEntity(null));
        }
    }

    // =====================================================
    // toResponseDTO
    // =====================================================
    @Nested
    @DisplayName("toResponseDTO")
    class ToResponseDTO {

        @Test
        @DisplayName("deve copiar todos os campos da entity para o DTO de resposta")
        void copiaTodosOsCampos() {
            EmployeeResponseDTO dto = mapper.toResponseDTO(entityCompleta());

            assertNotNull(dto);
            assertEquals(1L, dto.getId());
            assertEquals(10, dto.getUserId());
            assertEquals(2, dto.getPositionId());
            assertEquals(3, dto.getWorkModelId());
            assertEquals(new BigDecimal("3500.50"), dto.getSalary());
            assertEquals(new BigDecimal("40.00"), dto.getWorkingHours());
            assertEquals(CRIADO_EM, dto.getCreatedAt());
        }

        @Test
        @DisplayName("deve aceitar entity ainda não persistida (id e createdAt nulos)")
        void aceitaEntityNaoPersistida() {
            EmployeeEntity entity = entityCompleta();
            entity.setId(null);
            entity.setCreatedAt(null);

            EmployeeResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getId());
            assertNull(dto.getCreatedAt());
            assertEquals(10, dto.getUserId());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            EmployeeEntity entity = entityCompleta();

            assertNotSame(mapper.toResponseDTO(entity), mapper.toResponseDTO(entity));
        }

        @Test
        @DisplayName("não deve alterar a entity original")
        void naoAlteraEntityOriginal() {
            EmployeeEntity entity = entityCompleta();

            mapper.toResponseDTO(entity);

            assertEquals(1L, entity.getId());
            assertEquals(10, entity.getUserId());
            assertEquals(new BigDecimal("3500.50"), entity.getSalary());
            assertEquals(CRIADO_EM, entity.getCreatedAt());
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando a entity for nula")
        void entityNulaLancaNPE() {
            assertThrows(NullPointerException.class, () -> mapper.toResponseDTO(null));
        }
    }

    // =====================================================
    // updateEntityFromDTO
    // =====================================================
    @Nested
    @DisplayName("updateEntityFromDTO")
    class UpdateEntityFromDTO {

        @Test
        @DisplayName("deve sobrescrever todos os campos editáveis da entity")
        void sobrescreveCampos() {
            EmployeeEntity entity = entityCompleta();
            EmployeeCreateRequestDTO dto = new EmployeeCreateRequestDTO();
            dto.setUserId(20);
            dto.setPositionId(5);
            dto.setWorkModelId(6);
            dto.setSalary(new BigDecimal("8000.00"));
            dto.setWorkingHours(new BigDecimal("30.00"));

            mapper.updateEntityFromDTO(dto, entity);

            assertEquals(20, entity.getUserId());
            assertEquals(5, entity.getPositionId());
            assertEquals(6, entity.getWorkModelId());
            assertEquals(new BigDecimal("8000.00"), entity.getSalary());
            assertEquals(new BigDecimal("30.00"), entity.getWorkingHours());
        }

        @Test
        @DisplayName("não deve alterar id nem createdAt")
        void preservaCamposGerados() {
            EmployeeEntity entity = entityCompleta();

            mapper.updateEntityFromDTO(dtoValido(), entity);

            assertEquals(1L, entity.getId());
            assertEquals(CRIADO_EM, entity.getCreatedAt());
        }

        @Test
        @DisplayName("deve atualizar a mesma instância (sem criar outra)")
        void atualizaMesmaInstancia() {
            EmployeeEntity entity = entityCompleta();
            EmployeeEntity referencia = entity;
            EmployeeCreateRequestDTO dto = dtoValido();
            dto.setSalary(new BigDecimal("9999.99"));

            mapper.updateEntityFromDTO(dto, entity);

            assertSame(referencia, entity);
            assertEquals(new BigDecimal("9999.99"), referencia.getSalary());
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            EmployeeCreateRequestDTO dto = dtoValido();

            mapper.updateEntityFromDTO(dto, entityCompleta());

            assertEquals(10, dto.getUserId());
            assertEquals(new BigDecimal("3500.50"), dto.getSalary());
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando o DTO for nulo")
        void dtoNuloLancaNPE() {
            assertThrows(NullPointerException.class,
                    () -> mapper.updateEntityFromDTO(null, entityCompleta()));
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando a entity for nula")
        void entityNulaLancaNPE() {
            assertThrows(NullPointerException.class,
                    () -> mapper.updateEntityFromDTO(dtoValido(), null));
        }
    }

    // =====================================================
    // Fluxo combinado
    // =====================================================
    @Test
    @DisplayName("toEntity seguido de toResponseDTO deve preservar os dados do DTO original")
    void roundTrip() {
        EmployeeEntity entity = mapper.toEntity(dtoValido());
        entity.setId(7L);               // simula o que o banco faria ao salvar
        entity.setCreatedAt(CRIADO_EM); // idem

        EmployeeResponseDTO response = mapper.toResponseDTO(entity);

        assertEquals(7L, response.getId());
        assertEquals(10, response.getUserId());
        assertEquals(2, response.getPositionId());
        assertEquals(3, response.getWorkModelId());
        assertEquals(new BigDecimal("3500.50"), response.getSalary());
        assertEquals(new BigDecimal("40.00"), response.getWorkingHours());
        assertEquals(CRIADO_EM, response.getCreatedAt());
    }
}