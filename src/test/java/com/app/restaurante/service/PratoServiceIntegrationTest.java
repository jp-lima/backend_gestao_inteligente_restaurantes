package com.app.restaurante.service;

import com.app.restaurante.dto.PratoCreateRequestDTO;
import com.app.restaurante.dto.PratoResponseDTO;
import com.app.restaurante.entity.CategoryEntity;
import com.app.restaurante.repository.CategoryRepository;
import com.app.restaurante.repository.PratoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração: sobe o contexto do Spring com H2 (src/test/resources/application.properties)
 * e usa o PratoService, o mapper e os repositories reais.
 *
 * Sem @Transactional de propósito: cada chamada ao service commita de verdade,
 * então o que se lê depois veio realmente do banco. O isolamento é feito limpando
 * as tabelas antes de cada teste.
 */
@SpringBootTest
class PratoServiceIntegrationTest {

    @Autowired
    private PratoService pratoService;

    @Autowired
    private PratoRepository pratoRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void limparBanco() {
        // importante: apagar primeiro a tabela filha (menu), depois a pai (categories)
        pratoRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    // ---------- helpers ----------

    private CategoryEntity categoriaPadrao() {
        return categoriaComTipo("Categoria Teste");
    }

    private CategoryEntity categoriaComTipo(String type) {
        CategoryEntity category = new CategoryEntity();
        try {
            Field typeField = CategoryEntity.class.getDeclaredField("type");
            typeField.setAccessible(true);
            typeField.set(category, type);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return categoryRepository.save(category);
    }

    private PratoCreateRequestDTO dto(String nome, BigDecimal preco, CategoryEntity categoria) {
        PratoCreateRequestDTO dto = new PratoCreateRequestDTO();
        dto.setName(nome);
        dto.setDescription("Descrição do " + nome);
        dto.setPrice(preco);
        dto.setCategory("Categoria do " + nome);
        dto.setCategoryId(categoria.getId());
        dto.setAvailable(true);
        dto.setImageUrl("http://example.com/" + nome.toLowerCase().replace(" ", "-") + ".png");
        return dto;
    }

    private PratoCreateRequestDTO dtoValido(CategoryEntity categoria) {
        return dto("Pizza Margherita", new BigDecimal("45.90"), categoria);
    }

    // =====================================================
    // create
    // =====================================================
    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("smoke test: cadastra um prato normalmente")
        void smokeTest() {
            CategoryEntity categoria = categoriaPadrao();

            PratoResponseDTO response = pratoService.create(dtoValido(categoria));

            assertNotNull(response.getId(), "o banco deve gerar o id");
            assertEquals("Pizza Margherita", response.getNome());
            assertEquals("Descrição do Pizza Margherita", response.getDescricao());
            assertEquals(new BigDecimal("45.90"), response.getPreco());
            assertEquals("Categoria do Pizza Margherita", response.getCategoria());
            assertEquals(categoria.getId(), response.getCategoryId());
            assertTrue(response.isDisponivel());
            assertNotNull(response.getImagemUrl());
            assertNotNull(response.getCreatedAt());
        }

        @Test
        @DisplayName("deve persistir o registro no banco")
        void persisteNoBanco() {
            CategoryEntity categoria = categoriaPadrao();

            pratoService.create(dtoValido(categoria));

            assertEquals(1, pratoRepository.count());
        }

        @Test
        @DisplayName("deve permitir cadastrar pratos diferentes")
        void pratosDiferentes() {
            CategoryEntity categoria = categoriaPadrao();

            PratoResponseDTO primeiro = pratoService.create(dto("Pizza", new BigDecimal("40.00"), categoria));
            PratoResponseDTO segundo = pratoService.create(dto("Lasanha", new BigDecimal("52.00"), categoria));

            assertNotEquals(primeiro.getId(), segundo.getId());
            assertEquals(2, pratoRepository.count());
        }

        @Test
        @DisplayName("deve rejeitar categoryId inexistente e não persistir nada")
        void categoryIdInexistente_rejeita() {
            PratoCreateRequestDTO dto = dtoValido(categoriaPadrao());
            dto.setCategoryId(999_999);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> pratoService.create(dto));

            assertEquals("Categoria não encontrada: 999999", ex.getMessage());
            assertEquals(0, pratoRepository.count());
            assertEquals(1, categoryRepository.count()); // a categoria criada no helper permanece
        }

        /*
         * REGRA DE NEGÓCIO: não pode existir dois pratos com o mesmo nome.
         *
         * ESTE TESTE FALHA HOJE: a entity PratoEntity não tem unique em "name",
         * então o service aceita o segundo cadastro.
         *
         * Para passar, a equipe precisa adicionar @Column(nullable = false, unique = true)
         * no campo name da PratoEntity (e uma migration, se usar Flyway/Liquibase em produção).
         */
        @Test
        @DisplayName("não deve permitir cadastrar dois pratos com o mesmo nome")
        void nomeDuplicado_deveFalhar() {
            CategoryEntity categoria = categoriaPadrao();
            pratoService.create(dto("Pizza", new BigDecimal("40.00"), categoria));

            assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                    () -> pratoService.create(dto("Pizza", new BigDecimal("52.00"), categoria)));

            assertEquals(1, pratoRepository.count(), "o segundo cadastro não pode ter sido gravado");
        }

    }

    // =====================================================
    // findById / findAll
    // =====================================================
    @Nested
    @DisplayName("findById e findAll")
    class Consultas {

        @Test
        @DisplayName("findById deve retornar o prato cadastrado")
        void findById_existente() {
            CategoryEntity categoria = categoriaPadrao();
            PratoResponseDTO criado = pratoService.create(dtoValido(categoria));

            Optional<PratoResponseDTO> encontrado = pratoService.findById(criado.getId());

            assertTrue(encontrado.isPresent());
            assertEquals(criado.getId(), encontrado.get().getId());
            assertEquals("Pizza Margherita", encontrado.get().getNome());
            assertEquals(new BigDecimal("45.90"), encontrado.get().getPreco());
        }

        @Test
        @DisplayName("findById com id inexistente deve retornar Optional vazio")
        void findById_inexistente() {
            Optional<PratoResponseDTO> encontrado = pratoService.findById(999_999);

            assertTrue(encontrado.isEmpty());
        }

        @Test
        @DisplayName("findAll deve retornar lista vazia quando não há pratos")
        void findAll_vazio() {
            assertTrue(pratoService.findAll().isEmpty());
        }

        @Test
        @DisplayName("findAll deve retornar todos os pratos cadastrados")
        void findAll_comRegistros() {
            CategoryEntity categoria = categoriaPadrao();
            pratoService.create(dto("Pizza", new BigDecimal("40.00"), categoria));
            pratoService.create(dto("Lasanha", new BigDecimal("52.00"), categoria));

            List<PratoResponseDTO> todos = pratoService.findAll();

            assertEquals(2, todos.size());
            assertTrue(todos.stream().anyMatch(p -> p.getNome().equals("Pizza")));
            assertTrue(todos.stream().anyMatch(p -> p.getNome().equals("Lasanha")));
        }
    }

    // =====================================================
    // update
    // =====================================================
    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("deve alterar o prato e persistir no banco, sem criar outro registro")
        void atualizaEPersiste() {
            CategoryEntity categoria = categoriaPadrao();
            PratoResponseDTO criado = pratoService.create(dtoValido(categoria));

            PratoCreateRequestDTO novo = dto("Lasanha", new BigDecimal("52.00"), categoria);

            Optional<PratoResponseDTO> atualizado = pratoService.update(criado.getId(), novo);

            assertTrue(atualizado.isPresent());
            assertEquals(criado.getId(), atualizado.get().getId());
            assertEquals(1, pratoRepository.count());

            // relê do banco para provar que persistiu
            Optional<PratoResponseDTO> doBanco = pratoService.findById(criado.getId());
            assertTrue(doBanco.isPresent());
            assertEquals("Lasanha", doBanco.get().getNome());
            assertEquals(new BigDecimal("52.00"), doBanco.get().getPreco());
        }

        @Test
        @DisplayName("update não deve alterar o createdAt")
        void updateMantemCreatedAt() {
            CategoryEntity categoria = categoriaPadrao();
            PratoResponseDTO criado = pratoService.create(dtoValido(categoria));
            var createdAtAntes = pratoService.findById(criado.getId()).orElseThrow().getCreatedAt();

            pratoService.update(criado.getId(), dto("Novo nome", new BigDecimal("99.00"), categoria));

            var createdAtDepois = pratoService.findById(criado.getId()).orElseThrow().getCreatedAt();
            assertEquals(createdAtAntes, createdAtDepois);
        }

        @Test
        @DisplayName("update com id inexistente deve retornar Optional vazio e não criar registro")
        void update_inexistente() {
            CategoryEntity categoria = categoriaPadrao();

            Optional<PratoResponseDTO> atualizado = pratoService.update(999_999, dtoValido(categoria));

            assertTrue(atualizado.isEmpty());
            assertEquals(0, pratoRepository.count());
        }

        @Test
        @DisplayName("update com categoryId inexistente deve lançar exceção e manter o prato original")
        void updateCategoryIdInexistente_rejeita() {
            CategoryEntity categoria = categoriaPadrao();
            PratoResponseDTO criado = pratoService.create(dtoValido(categoria));

            PratoCreateRequestDTO novo = dtoValido(categoria);
            novo.setCategoryId(999_999);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> pratoService.update(criado.getId(), novo));

            assertEquals("Categoria não encontrada: 999999", ex.getMessage());

            Optional<PratoResponseDTO> doBanco = pratoService.findById(criado.getId());
            assertTrue(doBanco.isPresent());
            assertEquals("Pizza Margherita", doBanco.get().getNome());
        }

        @Test
        @DisplayName("update deve permitir trocar a categoria")
        void updateTrocaCategoria() {
            CategoryEntity categoriaOriginal = categoriaPadrao();
            CategoryEntity novaCategoria = categoriaPadrao();
            PratoResponseDTO criado = pratoService.create(dtoValido(categoriaOriginal));

            PratoCreateRequestDTO novo = dtoValido(novaCategoria);

            Optional<PratoResponseDTO> atualizado = pratoService.update(criado.getId(), novo);

            assertTrue(atualizado.isPresent());
            assertEquals(novaCategoria.getId(), atualizado.get().getCategoryId());
        }

        @Test
        @DisplayName("update não deve afetar os outros pratos")
        void updateNaoAfetaOutros() {
            CategoryEntity categoria = categoriaPadrao();
            PratoResponseDTO primeiro = pratoService.create(dto("Pizza", new BigDecimal("40.00"), categoria));
            PratoResponseDTO segundo = pratoService.create(dto("Lasanha", new BigDecimal("52.00"), categoria));

            pratoService.update(primeiro.getId(), dto("Pizza Calabresa", new BigDecimal("45.00"), categoria));

            Optional<PratoResponseDTO> doBanco = pratoService.findById(segundo.getId());
            assertTrue(doBanco.isPresent());
            assertEquals("Lasanha", doBanco.get().getNome());
        }
    }

    // =====================================================
    // delete
    // =====================================================
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deve remover o prato do banco")
        void removePrato() {
            CategoryEntity categoria = categoriaPadrao();
            PratoResponseDTO criado = pratoService.create(dtoValido(categoria));

            boolean removido = pratoService.delete(criado.getId());

            assertTrue(removido);
            assertEquals(0, pratoRepository.count());
            assertTrue(pratoService.findById(criado.getId()).isEmpty());
        }

        @Test
        @DisplayName("delete com id inexistente deve retornar false")
        void delete_inexistente() {
            boolean removido = pratoService.delete(999_999);

            assertFalse(removido);
        }

        @Test
        @DisplayName("deve remover apenas o prato informado")
        void removeSomenteOInformado() {
            CategoryEntity categoria = categoriaPadrao();
            PratoResponseDTO primeiro = pratoService.create(dto("Pizza", new BigDecimal("40.00"), categoria));
            PratoResponseDTO segundo = pratoService.create(dto("Lasanha", new BigDecimal("52.00"), categoria));

            pratoService.delete(primeiro.getId());

            assertEquals(1, pratoRepository.count());
            assertTrue(pratoService.findById(segundo.getId()).isPresent());
        }

        @Test
        @DisplayName("deletar duas vezes o mesmo id deve retornar false na segunda")
        void deletarDuasVezes() {
            CategoryEntity categoria = categoriaPadrao();
            PratoResponseDTO criado = pratoService.create(dtoValido(categoria));

            assertTrue(pratoService.delete(criado.getId()));
            assertFalse(pratoService.delete(criado.getId()));
        }
    }
}
