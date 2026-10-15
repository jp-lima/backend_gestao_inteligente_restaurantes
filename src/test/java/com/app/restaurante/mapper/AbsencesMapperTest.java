package com.app.restaurante.mapper;

import com.app.restaurante.dto.AbsencesCreateRequestDTO;
import com.app.restaurante.dto.AbsencesResponseDTO;
import com.app.restaurante.entity.AbsenceStatus;
import com.app.restaurante.entity.AbsencesEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AbsencesMapperTest {

    private AbsencesMapper mapper;

    private static final OffsetDateTime CRIADO_EM = OffsetDateTime.parse("2026-10-01T10:30:00-03:00");
    private static final OffsetDateTime INICIO = OffsetDateTime.parse("2026-12-01T08:00:00-03:00");
    private static final OffsetDateTime TERMINO = OffsetDateTime.parse("2026-12-10T18:00:00-03:00");

    @BeforeEach
    void setUp() {
        mapper = new AbsencesMapper();
    }

    // ---------- helpers ----------

    private AbsencesCreateRequestDTO dtoValido() {
        AbsencesCreateRequestDTO dto = new AbsencesCreateRequestDTO();
        dto.setEmployeeId(1L);
        dto.setReason("Férias");
        dto.setDescription("Viagem em família");
        dto.setStartAt(INICIO);
        dto.setEndAt(TERMINO);
        return dto;
    }

    private AbsencesEntity entityCompleta() {
        AbsencesEntity entity = new AbsencesEntity();
        entity.setId(1L);
        entity.setEmployeeId(1L);
        entity.setReason("Férias");
        entity.setDescription("Viagem em família");
        entity.setStatus(AbsenceStatus.PEDIDO);
        entity.setStartAt(INICIO);
        entity.setEndAt(TERMINO);
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
            AbsencesEntity entity = mapper.toEntity(dtoValido());

            assertNotNull(entity);
            assertEquals(1L, entity.getEmployeeId());
            assertEquals("Férias", entity.getReason());
            assertEquals("Viagem em família", entity.getDescription());
            assertEquals(INICIO, entity.getStartAt());
            assertEquals(TERMINO, entity.getEndAt());
        }

        @Test
        @DisplayName("não deve preencher id, status nem createdAt")
        void naoPreencheCamposGerados() {
            AbsencesEntity entity = mapper.toEntity(dtoValido());

            assertNull(entity.getId());
            assertNull(entity.getStatus());
            assertNull(entity.getCreatedAt());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            AbsencesCreateRequestDTO dto = dtoValido();

            assertNotSame(mapper.toEntity(dto), mapper.toEntity(dto));
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            AbsencesCreateRequestDTO dto = dtoValido();

            mapper.toEntity(dto);

            assertEquals(1L, dto.getEmployeeId());
            assertEquals("Férias", dto.getReason());
            assertEquals(INICIO, dto.getStartAt());
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
            AbsencesResponseDTO dto = mapper.toResponseDTO(entityCompleta());

            assertNotNull(dto);
            assertEquals(1L, dto.getId());
            assertEquals(1L, dto.getEmployeeId());
            assertEquals("Férias", dto.getReason());
            assertEquals("Viagem em família", dto.getDescription());
            assertEquals(AbsenceStatus.PEDIDO, dto.getStatus());
            assertEquals(INICIO, dto.getStartAt());
            assertEquals(TERMINO, dto.getEndAt());
            assertEquals(CRIADO_EM, dto.getCreatedAt());
        }

        @Test
        @DisplayName("deve aceitar entity ainda não persistida (id, status e createdAt nulos)")
        void aceitaEntityNaoPersistida() {
            AbsencesEntity entity = entityCompleta();
            entity.setId(null);
            entity.setStatus(null);
            entity.setCreatedAt(null);

            AbsencesResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getId());
            assertNull(dto.getStatus());
            assertNull(dto.getCreatedAt());
            assertEquals(1L, dto.getEmployeeId());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            AbsencesEntity entity = entityCompleta();

            assertNotSame(mapper.toResponseDTO(entity), mapper.toResponseDTO(entity));
        }

        @Test
        @DisplayName("não deve alterar a entity original")
        void naoAlteraEntityOriginal() {
            AbsencesEntity entity = entityCompleta();

            mapper.toResponseDTO(entity);

            assertEquals(1L, entity.getId());
            assertEquals("Férias", entity.getReason());
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
        @DisplayName("deve sobrescrever os campos editáveis da entity")
        void sobrescreveCampos() {
            AbsencesEntity entity = entityCompleta();
            AbsencesCreateRequestDTO dto = new AbsencesCreateRequestDTO();
            dto.setEmployeeId(99L); // deve ser ignorado pelo update
            dto.setReason("Licença médica");
            dto.setDescription("Atestado enviado");
            dto.setStartAt(INICIO.plusDays(1));
            dto.setEndAt(TERMINO.plusDays(1));

            mapper.updateEntityFromDTO(dto, entity);

            assertEquals("Licença médica", entity.getReason());
            assertEquals("Atestado enviado", entity.getDescription());
            assertEquals(INICIO.plusDays(1), entity.getStartAt());
            assertEquals(TERMINO.plusDays(1), entity.getEndAt());
        }

        @Test
        @DisplayName("não deve alterar id, employeeId, status nem createdAt")
        void preservaCamposImutaveis() {
            AbsencesEntity entity = entityCompleta();

            mapper.updateEntityFromDTO(dtoValido(), entity);

            assertEquals(1L, entity.getId());
            assertEquals(1L, entity.getEmployeeId());
            assertEquals(AbsenceStatus.PEDIDO, entity.getStatus());
            assertEquals(CRIADO_EM, entity.getCreatedAt());
        }

        @Test
        @DisplayName("deve atualizar a mesma instância (sem criar outra)")
        void atualizaMesmaInstancia() {
            AbsencesEntity entity = entityCompleta();
            AbsencesEntity referencia = entity;
            AbsencesCreateRequestDTO dto = dtoValido();
            dto.setReason("Outro motivo");

            mapper.updateEntityFromDTO(dto, entity);

            assertSame(referencia, entity);
            assertEquals("Outro motivo", referencia.getReason());
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            AbsencesCreateRequestDTO dto = dtoValido();

            mapper.updateEntityFromDTO(dto, entityCompleta());

            assertEquals("Férias", dto.getReason());
            assertEquals(INICIO, dto.getStartAt());
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
        AbsencesEntity entity = mapper.toEntity(dtoValido());
        entity.setId(7L);
        entity.setStatus(AbsenceStatus.PEDIDO);
        entity.setCreatedAt(CRIADO_EM);

        AbsencesResponseDTO response = mapper.toResponseDTO(entity);

        assertEquals(7L, response.getId());
        assertEquals(1L, response.getEmployeeId());
        assertEquals("Férias", response.getReason());
        assertEquals("Viagem em família", response.getDescription());
        assertEquals(AbsenceStatus.PEDIDO, response.getStatus());
        assertEquals(INICIO, response.getStartAt());
        assertEquals(TERMINO, response.getEndAt());
        assertEquals(CRIADO_EM, response.getCreatedAt());
    }
}
