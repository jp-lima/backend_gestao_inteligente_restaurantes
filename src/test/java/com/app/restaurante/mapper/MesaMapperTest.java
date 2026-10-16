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

    @BeforeEach
    void setUp() {
        mapper = new MesaMapper();
    }

    // ---------- helpers ----------

    private MesaCreateRequestDTO dtoValido() {
        MesaCreateRequestDTO dto = new MesaCreateRequestDTO();
        dto.setNumber(10);
        dto.setSeating_capacity(4);
        dto.setAvailable(true);
        return dto;
    }

    private MesaEntity entityCompleta() {
        MesaEntity entity = new MesaEntity();
        entity.setId(1);
        entity.setNumber(10);
        entity.setSeating_capacity(4);
        entity.setAvailable(true);
        entity.setCreated_at(CRIADO_EM);
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
            assertEquals(10, entity.getNumber());
            assertEquals(4, entity.getSeating_capacity());
            assertTrue(entity.getAvailable());
        }

        @Test
        @DisplayName("não deve preencher id nem created_at (são gerados pelo banco/Hibernate)")
        void naoPreencheCamposGerados() {
            MesaEntity entity = mapper.toEntity(dtoValido());

            assertNull(entity.getId());
            assertNull(entity.getCreated_at());
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

            assertEquals(10, dto.getNumber());
            assertEquals(4, dto.getSeating_capacity());
            assertTrue(dto.getAvailable());
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
            assertEquals(1, dto.getId());
            assertEquals(10, dto.getNumber());
            assertEquals(4, dto.getSeating_capacity());
            assertTrue(dto.getAvailable());
            assertEquals(CRIADO_EM, dto.getCreated_at());
        }

        @Test
        @DisplayName("deve aceitar entity ainda não persistida (id e timestamps nulos)")
        void aceitaEntityNaoPersistida() {
            MesaEntity entity = entityCompleta();
            entity.setId(null);
            entity.setCreated_at(null);

            MesaResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getId());
            assertNull(dto.getCreated_at());
            assertEquals(10, dto.getNumber());
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

            assertEquals(1, entity.getId());
            assertEquals(10, entity.getNumber());
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
            dto.setNumber(20);
            dto.setSeating_capacity(6);
            dto.setAvailable(false);

            mapper.updateEntityFromDTO(dto, entity);

            assertEquals(20, entity.getNumber());
            assertEquals(6, entity.getSeating_capacity());
            assertFalse(entity.getAvailable());
        }

        @Test
        @DisplayName("não deve alterar id nem created_at")
        void preservaCamposGerados() {
            MesaEntity entity = entityCompleta();

            mapper.updateEntityFromDTO(dtoValido(), entity);

            assertEquals(1, entity.getId());
            assertEquals(CRIADO_EM, entity.getCreated_at());
        }

        @Test
        @DisplayName("deve atualizar a mesma instância (sem criar outra)")
        void atualizaMesmaInstancia() {
            MesaEntity entity = entityCompleta();
            MesaEntity referencia = entity;
            MesaCreateRequestDTO dto = dtoValido();
            dto.setAvailable(false);

            mapper.updateEntityFromDTO(dto, entity);

            assertSame(referencia, entity);
            assertFalse(referencia.getAvailable());
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            MesaCreateRequestDTO dto = dtoValido();

            mapper.updateEntityFromDTO(dto, entityCompleta());

            assertEquals(10, dto.getNumber());
            assertTrue(dto.getAvailable());
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
        entity.setId(7);
        entity.setCreated_at(CRIADO_EM);

        MesaResponseDTO response = mapper.toResponseDTO(entity);

        assertEquals(7, response.getId());
        assertEquals(10, response.getNumber());
        assertEquals(4, response.getSeating_capacity());
        assertTrue(response.getAvailable());
        assertEquals(CRIADO_EM, response.getCreated_at());
    }
}
