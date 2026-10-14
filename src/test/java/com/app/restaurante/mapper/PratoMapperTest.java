package com.app.restaurante.mapper;

import com.app.restaurante.dto.PratoCreateRequestDTO;
import com.app.restaurante.dto.PratoResponseDTO;
import com.app.restaurante.entity.CategoryEntity;
import com.app.restaurante.entity.PratoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PratoMapperTest {

    private PratoMapper mapper;

    private static final LocalDateTime CRIADO_EM = LocalDateTime.parse("2026-10-01T10:30:00");

    @BeforeEach
    void setUp() {
        mapper = new PratoMapper();
    }

    // ---------- helpers ----------

    private CategoryEntity categoriaComId(Integer id) {
        CategoryEntity category = new CategoryEntity();
        try {
            Field idField = CategoryEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    private PratoCreateRequestDTO dtoValido() {
        PratoCreateRequestDTO dto = new PratoCreateRequestDTO();
        dto.setName("Pizza Margherita");
        dto.setDescription("Massa fina, molho de tomate e mussarela");
        dto.setPrice(new BigDecimal("45.90"));
        dto.setCategory("Pizzas");
        dto.setCategoryId(1);
        dto.setAvailable(true);
        dto.setImageUrl("http://example.com/pizza.png");
        return dto;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private PratoEntity pratoEntityCompleto() {
        PratoEntity entity = new PratoEntity();
        setField(entity, "id", 1);
        entity.setName("Pizza Margherita");
        entity.setDescription("Massa fina, molho de tomate e mussarela");
        entity.setPrice(new BigDecimal("45.90"));
        entity.setCategory("Pizzas");
        entity.setCategoryEntity(categoriaComId(10));
        entity.setAvailable(true);
        entity.setImageUrl("http://example.com/pizza.png");
        setField(entity, "createdAt", CRIADO_EM);
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
            CategoryEntity categoria = categoriaComId(10);

            PratoEntity entity = mapper.toEntity(dtoValido(), categoria);

            assertNotNull(entity);
            assertEquals("Pizza Margherita", entity.getName());
            assertEquals("Massa fina, molho de tomate e mussarela", entity.getDescription());
            assertEquals(new BigDecimal("45.90"), entity.getPrice());
            assertEquals("Pizzas", entity.getCategory());
            assertEquals(categoria, entity.getCategoryEntity());
            assertTrue(entity.isAvailable());
            assertEquals("http://example.com/pizza.png", entity.getImageUrl());
        }

        @Test
        @DisplayName("não deve preencher id nem createdAt (são gerados pelo banco/Hibernate)")
        void naoPreencheCamposGerados() {
            PratoEntity entity = mapper.toEntity(dtoValido(), categoriaComId(10));

            assertNull(entity.getId());
            assertNull(entity.getCreatedAt());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            PratoCreateRequestDTO dto = dtoValido();
            CategoryEntity categoria = categoriaComId(10);

            assertNotSame(mapper.toEntity(dto, categoria), mapper.toEntity(dto, categoria));
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            PratoCreateRequestDTO dto = dtoValido();

            mapper.toEntity(dto, categoriaComId(10));

            assertEquals("Pizza Margherita", dto.getName());
            assertEquals(new BigDecimal("45.90"), dto.getPrice());
            assertEquals("Pizzas", dto.getCategory());
        }

        @Test
        @DisplayName("não deve alterar a categoria recebida")
        void naoAlteraCategoria() {
            CategoryEntity categoria = categoriaComId(10);

            mapper.toEntity(dtoValido(), categoria);

            assertEquals(10, categoria.getId());
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando o DTO for nulo")
        void dtoNuloLancaNPE() {
            assertThrows(NullPointerException.class, () -> mapper.toEntity(null, categoriaComId(10)));
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
            PratoResponseDTO dto = mapper.toResponseDTO(pratoEntityCompleto());

            assertNotNull(dto);
            assertEquals(1, dto.getId());
            assertEquals("Pizza Margherita", dto.getNome());
            assertEquals("Massa fina, molho de tomate e mussarela", dto.getDescricao());
            assertEquals(new BigDecimal("45.90"), dto.getPreco());
            assertEquals("Pizzas", dto.getCategoria());
            assertEquals(10, dto.getCategoryId());
            assertTrue(dto.isDisponivel());
            assertEquals("http://example.com/pizza.png", dto.getImagemUrl());
            assertEquals(CRIADO_EM, dto.getCreatedAt());
        }

        @Test
        @DisplayName("deve aceitar entity ainda não persistida (id e createdAt nulos)")
        void aceitaEntityNaoPersistida() {
            PratoEntity entity = pratoEntityCompleto();
            setField(entity, "id", null);
            setField(entity, "createdAt", null);

            PratoResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getId());
            assertNull(dto.getCreatedAt());
            assertEquals("Pizza Margherita", dto.getNome());
        }

        @Test
        @DisplayName("deve aceitar description e imageUrl nulos")
        void aceitaCamposOpcionaisNulos() {
            PratoEntity entity = pratoEntityCompleto();
            entity.setDescription(null);
            entity.setImageUrl(null);

            PratoResponseDTO dto = mapper.toResponseDTO(entity);

            assertNull(dto.getDescricao());
            assertNull(dto.getImagemUrl());
        }

        @Test
        @DisplayName("deve retornar uma nova instância a cada chamada")
        void retornaNovaInstancia() {
            PratoEntity entity = pratoEntityCompleto();

            assertNotSame(mapper.toResponseDTO(entity), mapper.toResponseDTO(entity));
        }

        @Test
        @DisplayName("não deve alterar a entity original")
        void naoAlteraEntityOriginal() {
            PratoEntity entity = pratoEntityCompleto();

            mapper.toResponseDTO(entity);

            assertEquals(1, entity.getId());
            assertEquals("Pizza Margherita", entity.getName());
            assertEquals(new BigDecimal("45.90"), entity.getPrice());
        }

        /*
         * COMPORTAMENTO ATUAL: toResponseDTO chama entity.getCategoryEntity().getId()
         * sem verificar nulo. Se a entidade não tiver categoria, lança NPE.
         */
        @Test
        @DisplayName("deve lançar NullPointerException quando categoryEntity for nulo")
        void categoryEntityNuloLancaNPE() {
            PratoEntity entity = pratoEntityCompleto();
            entity.setCategoryEntity(null);

            assertThrows(NullPointerException.class, () -> mapper.toResponseDTO(entity));
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando a entity for nula")
        void entityNulaLancaNPE() {
            assertThrows(NullPointerException.class, () -> mapper.toResponseDTO(null));
        }
    }

    // =====================================================
    // updateEntity
    // =====================================================
    @Nested
    @DisplayName("updateEntity")
    class UpdateEntity {

        @Test
        @DisplayName("deve sobrescrever todos os campos editáveis da entity")
        void sobrescreveCampos() {
            PratoEntity entity = pratoEntityCompleto();
            PratoCreateRequestDTO dto = new PratoCreateRequestDTO();
            dto.setName("Lasanha");
            dto.setDescription("Lasanha à bolonhesa");
            dto.setPrice(new BigDecimal("52.00"));
            dto.setCategory("Massas");
            dto.setCategoryId(2);
            dto.setAvailable(false);
            dto.setImageUrl("http://example.com/lasanha.png");

            CategoryEntity novaCategoria = categoriaComId(20);
            mapper.updateEntity(entity, dto, novaCategoria);

            assertEquals("Lasanha", entity.getName());
            assertEquals("Lasanha à bolonhesa", entity.getDescription());
            assertEquals(new BigDecimal("52.00"), entity.getPrice());
            assertEquals("Massas", entity.getCategory());
            assertEquals(novaCategoria, entity.getCategoryEntity());
            assertFalse(entity.isAvailable());
            assertEquals("http://example.com/lasanha.png", entity.getImageUrl());
        }

        @Test
        @DisplayName("não deve alterar id nem createdAt")
        void preservaCamposGerados() {
            PratoEntity entity = pratoEntityCompleto();

            mapper.updateEntity(entity, dtoValido(), categoriaComId(99));

            assertEquals(1, entity.getId());
            assertEquals(CRIADO_EM, entity.getCreatedAt());
        }

        @Test
        @DisplayName("deve atualizar a mesma instância (sem criar outra)")
        void atualizaMesmaInstancia() {
            PratoEntity entity = pratoEntityCompleto();
            PratoEntity referencia = entity;

            mapper.updateEntity(entity, dtoValido(), categoriaComId(10));

            assertSame(referencia, entity);
        }

        @Test
        @DisplayName("não deve alterar o DTO recebido")
        void naoAlteraDto() {
            PratoCreateRequestDTO dto = dtoValido();

            mapper.updateEntity(pratoEntityCompleto(), dto, categoriaComId(10));

            assertEquals("Pizza Margherita", dto.getName());
            assertEquals(new BigDecimal("45.90"), dto.getPrice());
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando o DTO for nulo")
        void dtoNuloLancaNPE() {
            assertThrows(NullPointerException.class,
                    () -> mapper.updateEntity(pratoEntityCompleto(), null, categoriaComId(10)));
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando a entity for nula")
        void entityNulaLancaNPE() {
            assertThrows(NullPointerException.class,
                    () -> mapper.updateEntity(null, dtoValido(), categoriaComId(10)));
        }
    }

    // =====================================================
    // toResponseDTOList
    // =====================================================
    @Nested
    @DisplayName("toResponseDTOList")
    class ToResponseDTOList {

        @Test
        @DisplayName("lista vazia deve retornar lista vazia")
        void listaVazia() {
            assertTrue(mapper.toResponseDTOList(List.of()).isEmpty());
        }

        @Test
        @DisplayName("deve converter todos os itens mantendo a ordem")
        void converteTodos() {
            PratoEntity primeiro = pratoEntityCompleto();
            primeiro.setName("Pizza");

            PratoEntity segundo = pratoEntityCompleto();
            setField(segundo, "id", 2);
            segundo.setName("Lasanha");
            segundo.setCategoryEntity(categoriaComId(20));

            List<PratoResponseDTO> lista = mapper.toResponseDTOList(List.of(primeiro, segundo));

            assertEquals(2, lista.size());
            assertEquals("Pizza", lista.get(0).getNome());
            assertEquals("Lasanha", lista.get(1).getNome());
        }
    }

    // =====================================================
    // Fluxo combinado
    // =====================================================
    @Test
    @DisplayName("toEntity seguido de toResponseDTO deve preservar os dados do DTO original")
    void roundTrip() {
        PratoCreateRequestDTO dto = new PratoCreateRequestDTO();
        dto.setName("Hambúrguer");
        dto.setDescription("Pão, carne 180g e queijo");
        dto.setPrice(new BigDecimal("38.00"));
        dto.setCategory("Hambúrgueres");
        dto.setCategoryId(3);
        dto.setAvailable(true);
        dto.setImageUrl("http://example.com/burger.png");

        CategoryEntity categoria = categoriaComId(30);
        PratoEntity entity = mapper.toEntity(dto, categoria);
        setField(entity, "id", 7); // simula o que o banco faria ao salvar
        setField(entity, "createdAt", CRIADO_EM);

        PratoResponseDTO response = mapper.toResponseDTO(entity);

        assertEquals(7, response.getId());
        assertEquals("Hambúrguer", response.getNome());
        assertEquals("Pão, carne 180g e queijo", response.getDescricao());
        assertEquals(new BigDecimal("38.00"), response.getPreco());
        assertEquals("Hambúrgueres", response.getCategoria());
        assertEquals(30, response.getCategoryId());
        assertTrue(response.isDisponivel());
        assertEquals("http://example.com/burger.png", response.getImagemUrl());
        assertEquals(CRIADO_EM, response.getCreatedAt());
    }
}
