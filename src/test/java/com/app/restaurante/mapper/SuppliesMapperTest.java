package com.app.restaurante.mapper;

import com.app.restaurante.dto.SuppliesCreateRequestDTO;
import com.app.restaurante.dto.SuppliesResponseDTO;
import com.app.restaurante.entity.SuppliesEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SuppliesMapperTest {

    private SuppliesMapper mapper;

    private static final LocalDate VALIDADE = LocalDate.of(2030, 1, 15);
    private static final OffsetDateTime ATUALIZADO_EM = OffsetDateTime.parse("2026-10-01T10:30:00-03:00");

    @BeforeEach
    void setUp() {
        mapper = new SuppliesMapper();
    }

    // ---------- helpers ----------

    private SuppliesCreateRequestDTO dtoValido() {
        return new SuppliesCreateRequestDTO("Leite", 80, 5, "L", false, VALIDADE);
    }

    private SuppliesEntity entityCompleta() {
        return SuppliesEntity.builder()
                .id(1L)
                .name("Leite")
                .quantity(80)
                .minimum_stock(5)
                .unit("L")
                .perishable(true)
                .expiration_date(VALIDADE)
                .updated_at(ATUALIZADO_EM)
                .build();
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
            SuppliesEntity entity = mapper.toEntity(dtoValido());

            assertNotNull(entity);
            assertEquals("Leite", entity.getName());
            assertEquals(80, entity.getQuantity());
            assertEquals(5, entity.getMinimum_stock());
            assertEquals("L", entity.getUnit());
            assertFalse(entity.isPerishable());
            assertEquals(VALIDADE, entity.getExpiration_date());
        }

        @Test
        @DisplayName("não deve preencher id nem updated_at (são gerados pelo banco/Hibernate)")
        void naoPreencheCamposGerados() {
            SuppliesEntity entity = mapper.toEntity(dtoValido());

            assertNull(entity.getId());
            assertNull(entity.getUpdated_at());
        }

        @ParameterizedTest(name = "perishable = {0}")
        @ValueSource(booleans = {true, false})
        @DisplayName("deve copiar perishable verdadeiro e falso")
        void copiaPerishable(boolean perishable) {
            SuppliesCreateRequestDTO dto = dtoValido();
            dto.setPerishable(perishable);

            SuppliesEntity entity = mapper.toEntity(dto);

            assertEquals(perishable, entity.isPerishable());
        }

        @Test
        @DisplayName("deve aceitar expiration_date nula (item não perecível)")
        void aceitaValidadeNula() {
            SuppliesCreateRequestDTO dto = dtoValido();
            dto.setExpiration_date(null);

            SuppliesEntity entity = mapper.toEntity(dto);

            assertNull(entity.getExpiration_date());
        }

        @Test
        @DisplayName("deve aceitar valores de borda (quantity e minimum_stock zerados)")
        void aceitaValoresZerados() {
            SuppliesCreateRequestDTO dto = dtoValido();
            dto.setQuantity(0);
            dto.setMinimumStock(0);

            SuppliesEntity entity = mapper.toEntity(dto);

            assertEquals(0, entity.getQuantity());
            assertEquals(0, entity.getMinimum_stock());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            SuppliesCreateRequestDTO dto = dtoValido();

            assertNotSame(mapper.toEntity(dto), mapper.toEntity(dto));
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando perishable do DTO for nulo")
        void perishableNuloLancaNPE() {
            // Documenta o comportamento atual: unboxing de Boolean null -> boolean.
            // Se corrigir o mapper (ou usar @NotNull no DTO), ajuste este teste.
            SuppliesCreateRequestDTO dto = dtoValido();
            dto.setPerishable(null);

            assertThrows(NullPointerException.class, () -> mapper.toEntity(dto));
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
            SuppliesResponseDTO dto = mapper.toResponseDTO(entityCompleta());

            assertNotNull(dto);
            assertEquals(1L, dto.getId());
            assertEquals("Leite", dto.getName());
            assertEquals(80, dto.getQuantity());
            assertEquals(5, dto.getMinimumStock());
            assertEquals("L", dto.getUnit());
            assertTrue(dto.getPerishable());
            assertEquals(VALIDADE, dto.getExpiration_date());
            assertEquals(ATUALIZADO_EM, dto.getUpdatedAt());
        }

        @ParameterizedTest(name = "perishable = {0}")
        @ValueSource(booleans = {true, false})
        @DisplayName("deve copiar perishable verdadeiro e falso")
        void copiaPerishable(boolean perishable) {
            SuppliesEntity entity = entityCompleta();
            entity.setPerishable(perishable);

            SuppliesResponseDTO dto = mapper.toResponseDTO(entity);

            assertEquals(perishable, dto.getPerishable());
        }

        @Test
        @DisplayName("deve aceitar expiration_date nula")
        void aceitaValidadeNula() {
            SuppliesEntity entity = entityCompleta();
            entity.setExpiration_date(null);

            SuppliesResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getExpiration_date());
        }

        @Test
        @DisplayName("deve aceitar id e updated_at nulos (entity ainda não persistida)")
        void aceitaEntityNaoPersistida() {
            SuppliesEntity entity = entityCompleta();
            entity.setId(null);
            entity.setUpdated_at(null);

            SuppliesResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getId());
            assertNull(dto.getUpdatedAt());
            assertEquals("Leite", dto.getName());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            SuppliesEntity entity = entityCompleta();

            assertNotSame(mapper.toResponseDTO(entity), mapper.toResponseDTO(entity));
        }

        @Test
        @DisplayName("não deve alterar a entity original")
        void naoAlteraEntityOriginal() {
            SuppliesEntity entity = entityCompleta();

            mapper.toResponseDTO(entity);

            assertEquals(1L, entity.getId());
            assertEquals("Leite", entity.getName());
            assertEquals(80, entity.getQuantity());
            assertEquals(ATUALIZADO_EM, entity.getUpdated_at());
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
            SuppliesEntity entity = entityCompleta();
            SuppliesCreateRequestDTO dto = new SuppliesCreateRequestDTO(
                    "Farinha", 10, 2, "KG", false, LocalDate.of(2031, 6, 1));

            mapper.updateEntityFromDTO(dto, entity);

            assertEquals("Farinha", entity.getName());
            assertEquals(10, entity.getQuantity());
            assertEquals(2, entity.getMinimum_stock());
            assertEquals("KG", entity.getUnit());
            assertFalse(entity.isPerishable());
            assertEquals(LocalDate.of(2031, 6, 1), entity.getExpiration_date());
        }

        @Test
        @DisplayName("não deve alterar id nem updated_at")
        void preservaCamposGerados() {
            SuppliesEntity entity = entityCompleta();

            mapper.updateEntityFromDTO(dtoValido(), entity);

            assertEquals(1L, entity.getId());
            assertEquals(ATUALIZADO_EM, entity.getUpdated_at());
        }

        @Test
        @DisplayName("deve atualizar a mesma instância (sem criar outra)")
        void atualizaMesmaInstancia() {
            SuppliesEntity entity = entityCompleta();
            SuppliesEntity referencia = entity;

            mapper.updateEntityFromDTO(dtoValido(), entity);

            assertSame(referencia, entity);
            assertEquals("Leite", referencia.getName());
        }

        @Test
        @DisplayName("deve limpar expiration_date quando o DTO vier com data nula")
        void limpaValidadeQuandoNula() {
            SuppliesEntity entity = entityCompleta();
            SuppliesCreateRequestDTO dto = dtoValido();
            dto.setExpiration_date(null);

            mapper.updateEntityFromDTO(dto, entity);

            assertNull(entity.getExpiration_date());
        }

        @ParameterizedTest(name = "perishable = {0}")
        @ValueSource(booleans = {true, false})
        @DisplayName("deve atualizar perishable verdadeiro e falso")
        void atualizaPerishable(boolean perishable) {
            SuppliesEntity entity = entityCompleta();
            entity.setPerishable(!perishable);
            SuppliesCreateRequestDTO dto = dtoValido();
            dto.setPerishable(perishable);

            mapper.updateEntityFromDTO(dto, entity);

            assertEquals(perishable, entity.isPerishable());
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            SuppliesCreateRequestDTO dto = dtoValido();

            mapper.updateEntityFromDTO(dto, entityCompleta());

            assertEquals("Leite", dto.getName());
            assertEquals(80, dto.getQuantity());
            assertEquals(5, dto.getMinimumStock());
            assertEquals("L", dto.getUnit());
            assertEquals(VALIDADE, dto.getExpiration_date());
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando perishable do DTO for nulo")
        void perishableNuloLancaNPE() {
            SuppliesCreateRequestDTO dto = dtoValido();
            dto.setPerishable(null);

            assertThrows(NullPointerException.class,
                    () -> mapper.updateEntityFromDTO(dto, entityCompleta()));
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
        SuppliesCreateRequestDTO dto = new SuppliesCreateRequestDTO(
                "Queijo", 12, 3, "KG", true, VALIDADE);

        SuppliesEntity entity = mapper.toEntity(dto);
        entity.setId(7L); // simula o que o banco faria ao salvar
        SuppliesResponseDTO response = mapper.toResponseDTO(entity);

        assertEquals(7L, response.getId());
        assertEquals("Queijo", response.getName());
        assertEquals(12, response.getQuantity());
        assertEquals(3, response.getMinimumStock());
        assertEquals("KG", response.getUnit());
        assertTrue(response.getPerishable());
        assertEquals(VALIDADE, response.getExpiration_date());
    }
}