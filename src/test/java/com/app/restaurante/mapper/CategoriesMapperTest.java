package com.app.restaurante.mapper;

import com.app.restaurante.dto.CategoriesCreateRequestDTO;
import com.app.restaurante.dto.CategoriesResponseDTO;
import com.app.restaurante.entity.CategoriesEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CategoriesMapperTest {

    private CategoriesMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoriesMapper();
    }

    // =====================================================
    // toEntity
    // =====================================================
    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("deve copiar o type do DTO para a entity")
        void copiaType() {
            CategoriesEntity entity = mapper.toEntity(new CategoriesCreateRequestDTO("Bebidas"));

            assertNotNull(entity);
            assertEquals("Bebidas", entity.getType());
        }

        @Test
        @DisplayName("não deve preencher o id (é gerado pelo banco)")
        void naoPreencheId() {
            CategoriesEntity entity = mapper.toEntity(new CategoriesCreateRequestDTO("Bebidas"));

            assertNull(entity.getId());
        }

        @ParameterizedTest(name = "type = [{0}]")
        @ValueSource(strings = {"", " ", "Açaí & Cia", "Sobremesas e doces"})
        @DisplayName("deve copiar o type exatamente como veio, sem tratar nem validar")
        void copiaTypeSemAlterar(String type) {
            CategoriesEntity entity = mapper.toEntity(new CategoriesCreateRequestDTO(type));

            assertEquals(type, entity.getType());
        }

        @Test
        @DisplayName("deve aceitar type nulo (a validação é responsabilidade do DTO/banco)")
        void aceitaTypeNulo() {
            CategoriesEntity entity = mapper.toEntity(new CategoriesCreateRequestDTO(null));

            assertNull(entity.getType());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO("Bebidas");

            assertNotSame(mapper.toEntity(dto), mapper.toEntity(dto));
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
        @DisplayName("deve copiar id e type da entity para o DTO de resposta")
        void copiaCampos() {
            CategoriesEntity entity = CategoriesEntity.builder().id(1L).type("Bebidas").build();

            CategoriesResponseDTO dto = mapper.toResponseDTO(entity);

            assertNotNull(dto);
            assertEquals(1L, dto.getId());
            assertEquals("Bebidas", dto.getType());
        }

        @Test
        @DisplayName("deve aceitar entity ainda não persistida (id nulo)")
        void aceitaIdNulo() {
            CategoriesEntity entity = CategoriesEntity.builder().type("Bebidas").build();

            CategoriesResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getId());
            assertEquals("Bebidas", dto.getType());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            CategoriesEntity entity = CategoriesEntity.builder().id(1L).type("Bebidas").build();

            assertNotSame(mapper.toResponseDTO(entity), mapper.toResponseDTO(entity));
        }

        @Test
        @DisplayName("não deve alterar a entity original")
        void naoAlteraEntity() {
            CategoriesEntity entity = CategoriesEntity.builder().id(1L).type("Bebidas").build();

            mapper.toResponseDTO(entity);

            assertEquals(1L, entity.getId());
            assertEquals("Bebidas", entity.getType());
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
        @DisplayName("deve sobrescrever o type da entity")
        void sobrescreveType() {
            CategoriesEntity entity = CategoriesEntity.builder().id(1L).type("Bebidas").build();

            mapper.updateEntityFromDTO(new CategoriesCreateRequestDTO("Sobremesas"), entity);

            assertEquals("Sobremesas", entity.getType());
        }

        @Test
        @DisplayName("não deve alterar o id")
        void preservaId() {
            CategoriesEntity entity = CategoriesEntity.builder().id(1L).type("Bebidas").build();

            mapper.updateEntityFromDTO(new CategoriesCreateRequestDTO("Sobremesas"), entity);

            assertEquals(1L, entity.getId());
        }

        @Test
        @DisplayName("deve atualizar a mesma instância (sem criar outra)")
        void atualizaMesmaInstancia() {
            CategoriesEntity entity = CategoriesEntity.builder().id(1L).type("Bebidas").build();
            CategoriesEntity referencia = entity;

            mapper.updateEntityFromDTO(new CategoriesCreateRequestDTO("Sobremesas"), entity);

            assertSame(referencia, entity);
            assertEquals("Sobremesas", referencia.getType());
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO("Sobremesas");

            mapper.updateEntityFromDTO(dto, CategoriesEntity.builder().id(1L).type("Bebidas").build());

            assertEquals("Sobremesas", dto.getType());
        }

        @Test
        @DisplayName("deve limpar o type quando o DTO vier com type nulo")
        void typeNuloLimpaCampo() {
            CategoriesEntity entity = CategoriesEntity.builder().id(1L).type("Bebidas").build();

            mapper.updateEntityFromDTO(new CategoriesCreateRequestDTO(null), entity);

            assertNull(entity.getType());
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando o DTO for nulo")
        void dtoNuloLancaNPE() {
            CategoriesEntity entity = CategoriesEntity.builder().id(1L).type("Bebidas").build();

            assertThrows(NullPointerException.class, () -> mapper.updateEntityFromDTO(null, entity));
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando a entity for nula")
        void entityNulaLancaNPE() {
            CategoriesCreateRequestDTO dto = new CategoriesCreateRequestDTO("Sobremesas");

            assertThrows(NullPointerException.class, () -> mapper.updateEntityFromDTO(dto, null));
        }
    }

    // =====================================================
    // Fluxo combinado
    // =====================================================
    @Test
    @DisplayName("toEntity seguido de toResponseDTO deve preservar o type do DTO original")
    void roundTrip() {
        CategoriesEntity entity = mapper.toEntity(new CategoriesCreateRequestDTO("Bebidas"));
        entity.setId(7L); // simula o que o banco faria ao salvar

        CategoriesResponseDTO response = mapper.toResponseDTO(entity);

        assertEquals(7L, response.getId());
        assertEquals("Bebidas", response.getType());
    }
}
