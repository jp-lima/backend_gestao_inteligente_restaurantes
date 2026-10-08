package com.app.restaurante.service;

import com.app.restaurante.dto.CategoriesCreateRequestDTO;
import com.app.restaurante.dto.CategoriesResponseDTO;
import com.app.restaurante.repository.CategoriesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração: sobe o contexto do Spring com H2 (src/test/resources/application.properties)
 * e usa o CategoriesService, o mapper e o repository reais.
 *
 * Sem @Transactional de propósito: cada chamada ao service commita de verdade,
 * então o que se lê depois veio realmente do banco. O isolamento é feito limpando
 * a tabela antes de cada teste.
 */
@SpringBootTest
class CategoriesServiceIntegrationTest {

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @BeforeEach
    void limparBanco() {
        categoriesRepository.deleteAll();
    }

    // ---------- helpers ----------

    private CategoriesCreateRequestDTO dto(String type) {
        return new CategoriesCreateRequestDTO(type);
    }

    // =====================================================
    // create
    // =====================================================
    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("smoke test: cadastra uma categoria normalmente")
        void smokeTest() {
            CategoriesResponseDTO response = categoriesService.create(dto("Bebidas"));

            assertNotNull(response.getId(), "o banco deve gerar o id");
            assertEquals("Bebidas", response.getType());
        }

        @Test
        @DisplayName("deve persistir o registro no banco")
        void persisteNoBanco() {
            CategoriesResponseDTO response = categoriesService.create(dto("Bebidas"));

            assertEquals(1, categoriesRepository.count());
            assertTrue(categoriesRepository.findById(response.getId()).isPresent());
        }

        @Test
        @DisplayName("deve permitir cadastrar categorias com types diferentes")
        void typesDiferentes() {
            CategoriesResponseDTO bebidas = categoriesService.create(dto("Bebidas"));
            CategoriesResponseDTO sobremesas = categoriesService.create(dto("Sobremesas"));

            assertNotEquals(bebidas.getId(), sobremesas.getId());
            assertEquals(2, categoriesRepository.count());
        }

        /*
         * COMPORTAMENTO ATUAL (a confirmar com a equipe): a entity não tem unique em "type",
         * então duas categorias com o mesmo nome são aceitas e viram dois registros.
         * Se a regra for proibir duplicidade (como em Supplies), este teste deve ser
         * trocado por um assertThrows, e a entity precisa de unique = true em type.
         */
        @Test
        @DisplayName("hoje permite cadastrar duas categorias com o mesmo type (sem regra de unicidade)")
        void typeDuplicado_hojeEhPermitido() {
            CategoriesResponseDTO primeira = categoriesService.create(dto("Bebidas"));
            CategoriesResponseDTO segunda = categoriesService.create(dto("Bebidas"));

            assertNotEquals(primeira.getId(), segunda.getId());
            assertEquals(2, categoriesRepository.count());
        }

        @Test
        @DisplayName("type nulo deve ser rejeitado pelo banco (nullable = false)")
        void typeNulo_rejeitadoPeloBanco() {
            assertThrows(DataIntegrityViolationException.class,
                    () -> categoriesService.create(dto(null)));

            assertEquals(0, categoriesRepository.count());
        }

        /*
         * ATENÇÃO: o @NotBlank do DTO só dispara no controller (com @Valid).
         * Chamando o service direto, string vazia passa e é gravada.
         * Este teste documenta esse comportamento; a validação de ponta a ponta
         * deve ser coberta por teste de controller (MockMvc).
         */
        @Test
        @DisplayName("service não valida o DTO: type vazio é gravado (validação fica no controller)")
        void typeVazio_servicoNaoValida() {
            CategoriesResponseDTO response = categoriesService.create(dto(""));

            assertNotNull(response.getId());
            assertEquals("", response.getType());
        }
    }

    // =====================================================
    // findById / findAll
    // =====================================================
    @Nested
    @DisplayName("findById e findAll")
    class Consultas {

        @Test
        @DisplayName("findById deve retornar a categoria cadastrada")
        void findById_existente() {
            CategoriesResponseDTO criada = categoriesService.create(dto("Bebidas"));

            CategoriesResponseDTO encontrada = categoriesService.findById(criada.getId());

            assertEquals(criada.getId(), encontrada.getId());
            assertEquals("Bebidas", encontrada.getType());
        }

        @Test
        @DisplayName("findById com id inexistente deve lançar EntityNotFoundException com o id na mensagem")
        void findById_inexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> categoriesService.findById(999_999L));

            assertEquals("Categoria não encontrada para o ID: 999999", ex.getMessage());
        }

        @Test
        @DisplayName("findAll deve retornar lista vazia quando não há categorias")
        void findAll_vazio() {
            assertTrue(categoriesService.findAll().isEmpty());
        }

        @Test
        @DisplayName("findAll deve retornar todas as categorias cadastradas")
        void findAll_comRegistros() {
            categoriesService.create(dto("Bebidas"));
            categoriesService.create(dto("Sobremesas"));

            List<CategoriesResponseDTO> todas = categoriesService.findAll();

            assertEquals(2, todas.size());
            assertTrue(todas.stream().anyMatch(c -> c.getType().equals("Bebidas")));
            assertTrue(todas.stream().anyMatch(c -> c.getType().equals("Sobremesas")));
        }
    }

    // =====================================================
    // update
    // =====================================================
    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("deve alterar a categoria e persistir no banco, sem criar outro registro")
        void atualizaEPersiste() {
            CategoriesResponseDTO criada = categoriesService.create(dto("Bebidas"));

            CategoriesResponseDTO atualizada = categoriesService.update(criada.getId(), dto("Bebidas geladas"));

            assertEquals(criada.getId(), atualizada.getId());
            assertEquals(1, categoriesRepository.count());

            // relê do banco para provar que persistiu
            assertEquals("Bebidas geladas", categoriesService.findById(criada.getId()).getType());
        }

        @Test
        @DisplayName("update com id inexistente deve lançar EntityNotFoundException")
        void update_inexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> categoriesService.update(999_999L, dto("Bebidas")));

            assertEquals("Categoria não encontrada para o ID: 999999", ex.getMessage());
            assertEquals(0, categoriesRepository.count());
        }

        @Test
        @DisplayName("update não deve afetar as outras categorias")
        void updateNaoAfetaOutras() {
            CategoriesResponseDTO bebidas = categoriesService.create(dto("Bebidas"));
            CategoriesResponseDTO sobremesas = categoriesService.create(dto("Sobremesas"));

            categoriesService.update(bebidas.getId(), dto("Bebidas geladas"));

            assertEquals("Sobremesas", categoriesService.findById(sobremesas.getId()).getType());
        }

        @Test
        @DisplayName("type nulo no update deve ser rejeitado pelo banco e manter o valor anterior")
        void updateComTypeNulo_rejeitado() {
            CategoriesResponseDTO criada = categoriesService.create(dto("Bebidas"));

            assertThrows(DataIntegrityViolationException.class,
                    () -> categoriesService.update(criada.getId(), dto(null)));

            assertEquals("Bebidas", categoriesService.findById(criada.getId()).getType());
        }
    }

    // =====================================================
    // delete
    // =====================================================
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deve remover a categoria do banco")
        void removeCategoria() {
            CategoriesResponseDTO criada = categoriesService.create(dto("Bebidas"));

            categoriesService.delete(criada.getId());

            assertEquals(0, categoriesRepository.count());
            assertThrows(EntityNotFoundException.class,
                    () -> categoriesService.findById(criada.getId()));
        }

        @Test
        @DisplayName("delete com id inexistente deve lançar EntityNotFoundException")
        void delete_inexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> categoriesService.delete(999_999L));

            assertEquals("Categoria não encontrada para o ID: 999999", ex.getMessage());
        }

        @Test
        @DisplayName("deve remover apenas a categoria informada")
        void removeSomenteACategoriaInformada() {
            CategoriesResponseDTO bebidas = categoriesService.create(dto("Bebidas"));
            CategoriesResponseDTO sobremesas = categoriesService.create(dto("Sobremesas"));

            categoriesService.delete(bebidas.getId());

            assertEquals(1, categoriesRepository.count());
            assertEquals("Sobremesas", categoriesService.findById(sobremesas.getId()).getType());
        }

        @Test
        @DisplayName("deletar duas vezes o mesmo id deve falhar na segunda")
        void deletarDuasVezes() {
            CategoriesResponseDTO criada = categoriesService.create(dto("Bebidas"));
            categoriesService.delete(criada.getId());

            assertThrows(EntityNotFoundException.class,
                    () -> categoriesService.delete(criada.getId()));
        }

        @Test
        @DisplayName("deve permitir recadastrar o mesmo type depois de removido")
        void recadastraAposRemover() {
            CategoriesResponseDTO criada = categoriesService.create(dto("Bebidas"));
            categoriesService.delete(criada.getId());

            CategoriesResponseDTO nova = categoriesService.create(dto("Bebidas"));

            assertNotEquals(criada.getId(), nova.getId());
            assertEquals(1, categoriesRepository.count());
        }
    }
}

