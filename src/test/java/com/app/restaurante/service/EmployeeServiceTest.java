package com.app.restaurante.service;

import com.app.restaurante.dto.EmployeeCreateRequestDTO;
import com.app.restaurante.dto.EmployeeResponseDTO;
import com.app.restaurante.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração: sobe o contexto do Spring com H2 (src/test/resources/application.properties)
 * e usa o EmployeeService, o mapper e o repository reais.
 *
 * Sem @Transactional de propósito: cada chamada ao service commita de verdade,
 * então o que se lê depois veio realmente do banco. O isolamento é feito limpando
 * a tabela antes de cada teste.
 *
 * Valores decimais são comparados com compareTo (e não equals), porque o banco pode
 * normalizar a escala (3500.5 vs 3500.50).
 */
@SpringBootTest
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void limparBanco() {
        employeeRepository.deleteAll();
    }

    // ---------- helpers ----------

    private EmployeeCreateRequestDTO dto(Integer userId) {
        EmployeeCreateRequestDTO dto = new EmployeeCreateRequestDTO();
        dto.setUserId(userId);
        dto.setPositionId(2);
        dto.setWorkModelId(3);
        dto.setSalary(new BigDecimal("3500.50"));
        dto.setWorkingHours(new BigDecimal("40.00"));
        return dto;
    }

    private void assertDecimalEquals(String esperado, BigDecimal atual) {
        assertNotNull(atual);
        assertEquals(0, new BigDecimal(esperado).compareTo(atual),
                "esperado " + esperado + " mas veio " + atual);
    }

    // =====================================================
    // create
    // =====================================================
    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("smoke test: cadastra um funcionário normalmente")
        void smokeTest() {
            EmployeeResponseDTO response = employeeService.create(dto(10));

            assertNotNull(response.getId(), "o banco deve gerar o id");
            assertEquals(10, response.getUserId());
            assertEquals(2, response.getPositionId());
            assertEquals(3, response.getWorkModelId());
            assertDecimalEquals("3500.50", response.getSalary());
            assertDecimalEquals("40.00", response.getWorkingHours());
        }

        /*
         * Assume que a entity preenche createdAt automaticamente (ex.: @CreationTimestamp).
         * Se ela depender de DEFAULT do banco (insertable = false), este assert precisa de ajuste.
         */
        @Test
        @DisplayName("deve preencher createdAt ao cadastrar")
        void preencheCreatedAt() {
            EmployeeResponseDTO response = employeeService.create(dto(10));

            assertNotNull(response.getCreatedAt());
        }

        @Test
        @DisplayName("deve persistir o registro no banco")
        void persisteNoBanco() {
            EmployeeResponseDTO response = employeeService.create(dto(10));

            assertEquals(1, employeeRepository.count());
            assertTrue(employeeRepository.findById(response.getId()).isPresent());
        }

        @Test
        @DisplayName("deve permitir cadastrar funcionários diferentes")
        void funcionariosDiferentes() {
            EmployeeResponseDTO primeiro = employeeService.create(dto(10));
            EmployeeResponseDTO segundo = employeeService.create(dto(11));

            assertNotEquals(primeiro.getId(), segundo.getId());
            assertEquals(2, employeeRepository.count());
        }
    }

    // =====================================================
    // findById / findAll
    // =====================================================
    @Nested
    @DisplayName("findById e findAll")
    class Consultas {

        @Test
        @DisplayName("findById deve retornar o funcionário cadastrado")
        void findById_existente() {
            EmployeeResponseDTO criado = employeeService.create(dto(10));

            EmployeeResponseDTO encontrado = employeeService.findById(criado.getId());

            assertEquals(criado.getId(), encontrado.getId());
            assertEquals(10, encontrado.getUserId());
            assertDecimalEquals("3500.50", encontrado.getSalary());
        }

        @Test
        @DisplayName("findById com id inexistente deve lançar EntityNotFoundException com o id na mensagem")
        void findById_inexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> employeeService.findById(999_999L));

            assertEquals("Funcionário não encontrado pelo ID: 999999", ex.getMessage());
        }

        @Test
        @DisplayName("findAll deve retornar lista vazia quando não há funcionários")
        void findAll_vazio() {
            assertTrue(employeeService.findAll().isEmpty());
        }

        @Test
        @DisplayName("findAll deve retornar todos os funcionários cadastrados")
        void findAll_comRegistros() {
            employeeService.create(dto(10));
            employeeService.create(dto(11));

            List<EmployeeResponseDTO> todos = employeeService.findAll();

            assertEquals(2, todos.size());
            assertTrue(todos.stream().anyMatch(e -> e.getUserId().equals(10)));
            assertTrue(todos.stream().anyMatch(e -> e.getUserId().equals(11)));
        }
    }

    // =====================================================
    // update
    // =====================================================
    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("deve alterar o funcionário e persistir no banco, sem criar outro registro")
        void atualizaEPersiste() {
            EmployeeResponseDTO criado = employeeService.create(dto(10));

            EmployeeCreateRequestDTO novo = new EmployeeCreateRequestDTO();
            novo.setUserId(20);
            novo.setPositionId(5);
            novo.setWorkModelId(6);
            novo.setSalary(new BigDecimal("8000.00"));
            novo.setWorkingHours(new BigDecimal("30.00"));

            EmployeeResponseDTO atualizado = employeeService.update(criado.getId(), novo);

            assertEquals(criado.getId(), atualizado.getId());
            assertEquals(1, employeeRepository.count());

            // relê do banco para provar que persistiu
            EmployeeResponseDTO doBanco = employeeService.findById(criado.getId());
            assertEquals(20, doBanco.getUserId());
            assertEquals(5, doBanco.getPositionId());
            assertEquals(6, doBanco.getWorkModelId());
            assertDecimalEquals("8000.00", doBanco.getSalary());
            assertDecimalEquals("30.00", doBanco.getWorkingHours());
        }

        @Test
        @DisplayName("update não deve alterar o createdAt")
        void updateMantemCreatedAt() {
            EmployeeResponseDTO criado = employeeService.create(dto(10));
            // compara leitura do banco com leitura do banco (a resposta do create vem da memória,
            // com precisão diferente da gravada)
            var createdAtAntes = employeeService.findById(criado.getId()).getCreatedAt();

            employeeService.update(criado.getId(), dto(20));

            var createdAtDepois = employeeService.findById(criado.getId()).getCreatedAt();
            assertEquals(createdAtAntes, createdAtDepois);
        }

        @Test
        @DisplayName("update com id inexistente deve lançar EntityNotFoundException e não criar registro")
        void update_inexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> employeeService.update(999_999L, dto(10)));

            assertEquals("Funcionário não encontrado pelo ID: 999999", ex.getMessage());
            assertEquals(0, employeeRepository.count());
        }

        @Test
        @DisplayName("update não deve afetar os outros funcionários")
        void updateNaoAfetaOutros() {
            EmployeeResponseDTO primeiro = employeeService.create(dto(10));
            EmployeeResponseDTO segundo = employeeService.create(dto(11));

            employeeService.update(primeiro.getId(), dto(99));

            assertEquals(11, employeeService.findById(segundo.getId()).getUserId());
        }
    }

    // =====================================================
    // delete
    // =====================================================
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deve remover o funcionário do banco")
        void removeFuncionario() {
            EmployeeResponseDTO criado = employeeService.create(dto(10));

            employeeService.delete(criado.getId());

            assertEquals(0, employeeRepository.count());
            assertThrows(EntityNotFoundException.class,
                    () -> employeeService.findById(criado.getId()));
        }

        @Test
        @DisplayName("delete com id inexistente deve lançar EntityNotFoundException")
        void delete_inexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> employeeService.delete(999_999L));

            assertEquals("Funcionário não encontrado pelo ID: 999999", ex.getMessage());
        }

        @Test
        @DisplayName("deve remover apenas o funcionário informado")
        void removeSomenteOInformado() {
            EmployeeResponseDTO primeiro = employeeService.create(dto(10));
            EmployeeResponseDTO segundo = employeeService.create(dto(11));

            employeeService.delete(primeiro.getId());

            assertEquals(1, employeeRepository.count());
            assertEquals(11, employeeService.findById(segundo.getId()).getUserId());
        }

        @Test
        @DisplayName("deletar duas vezes o mesmo id deve falhar na segunda")
        void deletarDuasVezes() {
            EmployeeResponseDTO criado = employeeService.create(dto(10));
            employeeService.delete(criado.getId());

            assertThrows(EntityNotFoundException.class,
                    () -> employeeService.delete(criado.getId()));
        }
    }
}