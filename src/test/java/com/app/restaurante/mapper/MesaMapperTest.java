package com.app.restaurante.mapper;

import com.app.restaurante.dto.MesaCreateRequestDTO;
import com.app.restaurante.dto.MesaResponseDTO;
import com.app.restaurante.entity.MesaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MesaMapperTest {

    private MesaMapper mapper;

    private static final LocalDateTime CRIADO_EM = LocalDateTime.parse("2026-10-01T10:30:00");
    private static final LocalDateTime ATUALIZADO_EM = LocalDateTime.parse("2026-10-10T15:45:00");

    @BeforeEach
    void setUp() {
        mapper = new MesaMapper();
    }

    // ---------- helpers ----------

    private MesaCreateRequestDTO dtoValido() {
        MesaCreateRequestDTO dto = new MesaCreateRequestDTO();
        dto.setNumero(10);
        dto.setCapacidade(4);
        dto.setStatus("LIVRE");
        return dto;
    }

    private MesaEntity entityCompleta() {
        MesaEntity entity = new MesaEntity();
        entity.setId(1L);
        entity.setNumero(10);
        entity.setCapacidade(4);
        entity.setStatus("LIVRE");
        entity.setCreated_at(CRIADO_EM);
        entity.setUpdated_at(ATUALIZADO_EM);
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
            MesaEntity entity = mapper.toEntity(dtoValido());

            assertNotNull(entity);
            assertEquals(10, entity.getNumero());
            assertEquals(4, entity.getCapacidade());
            assertEquals("LIVRE", entity.getStatus());
        }

        @Test
        @DisplayName("não deve preencher id, created_at nem updated_at (são gerados pelo banco/Hibernate)")
        void naoPreencheCamposGerados() {
            MesaEntity entity = mapper.toEntity(dtoValido());

            assertNull(entity.getId());
            assertNull(entity.getCreated_at());
            assertNull(entity.getUpdated_at());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            MesaCreateRequestDTO dto = dtoValido();

            assertNotSame(mapper.toEntity(dto), mapper.toEntity(dto));
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            MesaCreateRequestDTO dto = dtoValido();

            mapper.toEntity(dto);

            assertEquals(10, dto.getNumero());
            assertEquals(4, dto.getCapacidade());
            assertEquals("LIVRE", dto.getStatus());
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
            MesaResponseDTO dto = mapper.toResponseDTO(entityCompleta());

            assertNotNull(dto);
            assertEquals(1L, dto.getId());
            assertEquals(10, dto.getNumero());
            assertEquals(4, dto.getCapacidade());
            assertEquals("LIVRE", dto.getStatus());
            assertEquals(CRIADO_EM, dto.getCreated_at());
            assertEquals(ATUALIZADO_EM, dto.getUpdated_at());
        }

        @Test
        @DisplayName("deve aceitar entity ainda não persistida (id e timestamps nulos)")
        void aceitaEntityNaoPersistida() {
            MesaEntity entity = entityCompleta();
            entity.setId(null);
            entity.setCreated_at(null);
            entity.setUpdated_at(null);

            MesaResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getId());
            assertNull(dto.getCreated_at());
            assertNull(dto.getUpdated_at());
            assertEquals(10, dto.getNumero());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            MesaEntity entity = entityCompleta();

            assertNotSame(mapper.toResponseDTO(entity), mapper.toResponseDTO(entity));
        }

        @Test
        @DisplayName("não deve alterar a entity original")
        void naoAlteraEntityOriginal() {
            MesaEntity entity = entityCompleta();

            mapper.toResponseDTO(entity);

            assertEquals(1L, entity.getId());
            assertEquals(10, entity.getNumero());
            assertEquals(CRIADO_EM, entity.getCreated_at());
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
            MesaEntity entity = entityCompleta();
            MesaCreateRequestDTO dto = new MesaCreateRequestDTO();
            dto.setNumero(20);
            dto.setCapacidade(6);
            dto.setStatus("OCUPADA");

            mapper.updateEntityFromDTO(dto, entity);

            assertEquals(20, entity.getNumero());
            assertEquals(6, entity.getCapacidade());
            assertEquals("OCUPADA", entity.getStatus());
        }

        @Test
        @DisplayName("não deve alterar id, created_at nem updated_at")
        void preservaCamposGerados() {
            MesaEntity entity = entityCompleta();

            mapper.updateEntityFromDTO(dtoValido(), entity);

            assertEquals(1L, entity.getId());
            assertEquals(CRIADO_EM, entity.getCreated_at());
            assertEquals(ATUALIZADO_EM, entity.getUpdated_at());
        }

        @Test
        @DisplayName("deve atualizar a mesma instância (sem criar outra)")
        void atualizaMesmaInstancia() {
            MesaEntity entity = entityCompleta();
            MesaEntity referencia = entity;
            MesaCreateRequestDTO dto = dtoValido();
            dto.setStatus("RESERVADA");

            mapper.updateEntityFromDTO(dto, entity);

            assertSame(referencia, entity);
            assertEquals("RESERVADA", referencia.getStatus());
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            MesaCreateRequestDTO dto = dtoValido();

            mapper.updateEntityFromDTO(dto, entityCompleta());

            assertEquals(10, dto.getNumero());
            assertEquals("LIVRE", dto.getStatus());
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
        MesaEntity entity = mapper.toEntity(dtoValido());
        entity.setId(7L);
        entity.setCreated_at(CRIADO_EM);
        entity.setUpdated_at(ATUALIZADO_EM);

        MesaResponseDTO response = mapper.toResponseDTO(entity);

        assertEquals(7L, response.getId());
        assertEquals(10, response.getNumero());
        assertEquals(4, response.getCapacidade());
        assertEquals("LIVRE", response.getStatus());
        assertEquals(CRIADO_EM, response.getCreated_at());
        assertEquals(ATUALIZADO_EM, response.getUpdated_at());
    }
}
