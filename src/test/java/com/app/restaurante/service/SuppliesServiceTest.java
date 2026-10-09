package com.app.restaurante.service;

import com.app.restaurante.dto.SuppliesCreateRequestDTO;
import com.app.restaurante.dto.SuppliesResponseDTO;
import com.app.restaurante.repository.SuppliesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração: sobe o contexto do Spring com H2 (src/test/resources/application.properties)
 * e usa o SuppliesService, o mapper e o repository reais.
 *
 * Sem @Transactional de propósito: cada chamada ao service commita de verdade,
 * então o que se lê depois veio realmente do banco. O isolamento é feito limpando
 * a tabela antes de cada teste.
 */
@SpringBootTest
class SuppliesServiceIntegrationTest {

    @Autowired
    private SuppliesService suppliesService;

    @Autowired
    private SuppliesRepository suppliesRepository;

    private static final LocalDate VALIDADE = LocalDate.now().plusDays(30);

    @BeforeEach
    void limparBanco() {
        suppliesRepository.deleteAll();
    }

    // ---------- helpers ----------

    private SuppliesCreateRequestDTO dto(String nome) {
        return new SuppliesCreateRequestDTO(nome, 80, 5, "L", false, VALIDADE);
    }

    // =====================================================
    // create
    // =====================================================
    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("smoke test: cadastra um insumo normalmente")
        void smokeTest() {
            SuppliesResponseDTO response = suppliesService.create(dto("Leite"));

            assertNotNull(response.getId(), "o banco deve gerar o id");
            assertEquals("Leite", response.getName());
            assertEquals(80, response.getQuantity());
            assertEquals(5, response.getMinimumStock());
            assertEquals("L", response.getUnit());
            assertFalse(response.getPerishable()); // ajuste para getPerishable() se for Boolean
            assertEquals(VALIDADE, response.getExpiration_date());
            assertNotNull(response.getUpdatedAt(), "@UpdateTimestamp deve preencher updatedAt");
        }

        @Test
        @DisplayName("deve persistir o registro no banco")
        void persisteNoBanco() {
            SuppliesResponseDTO response = suppliesService.create(dto("Leite"));

            assertEquals(1, suppliesRepository.count());
            assertTrue(suppliesRepository.findById(response.getId()).isPresent());
        }

        @Test
        @DisplayName("deve permitir cadastrar insumos com nomes diferentes")
        void nomesDiferentes() {
            SuppliesResponseDTO leite = suppliesService.create(dto("Leite"));
            SuppliesResponseDTO farinha = suppliesService.create(dto("Farinha"));

            assertNotEquals(leite.getId(), farinha.getId());
            assertEquals(2, suppliesRepository.count());
        }

        /*
         * REGRA DE NEGÓCIO (a confirmar com a equipe): não pode existir dois insumos com o mesmo nome.
         *
         * ESTE TESTE FALHA HOJE: o service não verifica duplicidade e a entity não tem
         * constraint de unicidade em "name", então o segundo cadastro é aceito.
         *
         * Para passar, a equipe precisa:
         *  1. adicionar unique no banco (@Column(nullable = false, unique = true) em name)
         *     + migration, se usarem Flyway/Liquibase em produção;
         *  2. decidir a exceção: aqui espera-se DataIntegrityViolationException (violação da
         *     constraint). Se criarem uma exceção de negócio (ex.: SuppliesAlreadyExistsException,
         *     mapeada para 409 Conflict), trocar a classe no assertThrows.
         */
        @Test
        @DisplayName("não deve permitir cadastrar dois insumos com o mesmo nome")
        void nomeDuplicado_deveFalhar() {
            suppliesService.create(dto("Leite"));

            assertThrows(DataIntegrityViolationException.class,
                    () -> suppliesService.create(dto("Leite")));

            assertEquals(1, suppliesRepository.count(), "o segundo cadastro não pode ter sido gravado");
        }
    }

    // =====================================================
    // findById / findAll
    // =====================================================
    @Nested
    @DisplayName("findById e findAll")
    class Consultas {

        @Test
        @DisplayName("findById deve retornar o insumo cadastrado")
        void findById_existente() {
            SuppliesResponseDTO criado = suppliesService.create(dto("Leite"));

            SuppliesResponseDTO encontrado = suppliesService.findById(criado.getId());

            assertEquals(criado.getId(), encontrado.getId());
            assertEquals("Leite", encontrado.getName());
        }

        @Test
        @DisplayName("findById com id inexistente deve lançar EntityNotFoundException")
        void findById_inexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> suppliesService.findById(999_999L));

            assertTrue(ex.getMessage().contains("999999"));
        }

        @Test
        @DisplayName("findAll deve retornar lista vazia quando não há insumos")
        void findAll_vazio() {
            assertTrue(suppliesService.findAll().isEmpty());
        }

        @Test
        @DisplayName("findAll deve retornar todos os insumos cadastrados")
        void findAll_comRegistros() {
            suppliesService.create(dto("Leite"));
            suppliesService.create(dto("Farinha"));

            List<SuppliesResponseDTO> todos = suppliesService.findAll();

            assertEquals(2, todos.size());
            assertTrue(todos.stream().anyMatch(s -> s.getName().equals("Leite")));
            assertTrue(todos.stream().anyMatch(s -> s.getName().equals("Farinha")));
        }
    }

    // =====================================================
    // update
    // =====================================================
    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("deve alterar o insumo e persistir no banco, sem criar outro registro")
        void atualizaEPersiste() {
            SuppliesResponseDTO criado = suppliesService.create(dto("Leite"));
            SuppliesCreateRequestDTO novo = new SuppliesCreateRequestDTO(
                    "Leite integral", 120, 10, "L", true, VALIDADE.plusDays(10));

            SuppliesResponseDTO atualizado = suppliesService.update(criado.getId(), novo);

            assertEquals(criado.getId(), atualizado.getId());
            assertEquals(1, suppliesRepository.count());

            // relê do banco para provar que persistiu
            SuppliesResponseDTO doBanco = suppliesService.findById(criado.getId());
            assertEquals("Leite integral", doBanco.getName());
            assertEquals(120, doBanco.getQuantity());
            assertEquals(10, doBanco.getMinimumStock());
            assertTrue(doBanco.getPerishable()); // ajuste para getPerishable() se for Boolean
            assertEquals(VALIDADE.plusDays(10), doBanco.getExpiration_date());
        }

        @Test
        @DisplayName("update com id inexistente deve lançar EntityNotFoundException")
        void update_inexistente() {
            assertThrows(EntityNotFoundException.class,
                    () -> suppliesService.update(999_999L, dto("Leite")));
        }

        @Test
        @DisplayName("deve permitir atualizar mantendo o próprio nome")
        void atualizaMantendoMesmoNome() {
            SuppliesResponseDTO criado = suppliesService.create(dto("Leite"));
            SuppliesCreateRequestDTO novo = new SuppliesCreateRequestDTO(
                    "Leite", 999, 5, "L", false, VALIDADE);

            SuppliesResponseDTO atualizado = suppliesService.update(criado.getId(), novo);

            assertEquals(999, atualizado.getQuantity());
        }

        /*
         * Mesma regra do cadastro: renomear um insumo para um nome que já existe
         * em OUTRO registro também não pode ser permitido.
         * ESTE TESTE FALHA HOJE (mesmo motivo do teste de duplicidade no create).
         */
        @Test
        @DisplayName("não deve permitir renomear para o nome de outro insumo")
        void renomearParaNomeExistente_deveFalhar() {
            suppliesService.create(dto("Leite"));
            SuppliesResponseDTO farinha = suppliesService.create(dto("Farinha"));

            assertThrows(DataIntegrityViolationException.class,
                    () -> suppliesService.update(farinha.getId(), dto("Leite")));

            assertEquals("Farinha", suppliesService.findById(farinha.getId()).getName());
        }
    }

    // =====================================================
    // delete
    // =====================================================
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deve remover o insumo do banco")
        void removeInsumo() {
            SuppliesResponseDTO criado = suppliesService.create(dto("Leite"));

            suppliesService.delete(criado.getId());

            assertEquals(0, suppliesRepository.count());
            assertThrows(EntityNotFoundException.class,
                    () -> suppliesService.findById(criado.getId()));
        }

        @Test
        @DisplayName("delete com id inexistente deve lançar EntityNotFoundException")
        void delete_inexistente() {
            assertThrows(EntityNotFoundException.class,
                    () -> suppliesService.delete(999_999L));
        }

        @Test
        @DisplayName("deve remover apenas o insumo informado")
        void removeSomenteOInformado() {
            SuppliesResponseDTO leite = suppliesService.create(dto("Leite"));
            SuppliesResponseDTO farinha = suppliesService.create(dto("Farinha"));

            suppliesService.delete(leite.getId());

            assertEquals(1, suppliesRepository.count());
            assertEquals("Farinha", suppliesService.findById(farinha.getId()).getName());
        }

        @Test
        @DisplayName("deve permitir recadastrar um nome depois de removido")
        void recadastraAposRemover() {
            SuppliesResponseDTO criado = suppliesService.create(dto("Leite"));
            suppliesService.delete(criado.getId());

            SuppliesResponseDTO novo = suppliesService.create(dto("Leite"));

            assertNotEquals(criado.getId(), novo.getId());
            assertEquals(1, suppliesRepository.count());
        }
    }
}