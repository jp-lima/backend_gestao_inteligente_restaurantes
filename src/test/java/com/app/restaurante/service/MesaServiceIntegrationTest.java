package com.app.restaurante.service;

import com.app.restaurante.dto.MesaCreateRequestDTO;
import com.app.restaurante.dto.MesaResponseDTO;
import com.app.restaurante.repository.MesaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração do CRUD de Mesas (padrão EmployeeService).
 * H2 em memória, sem @Transactional de propósito.
 */
@SpringBootTest
class MesaServiceIntegrationTest {

    @Autowired
    private MesaService mesaService;

    @Autowired
    private MesaRepository mesaRepository;

    @BeforeEach
    void limparBanco() {
        mesaRepository.deleteAll();
    }

    private MesaCreateRequestDTO dto(Integer numero, Integer capacidade, String status) {
        MesaCreateRequestDTO d = new MesaCreateRequestDTO();
        d.setNumero(numero);
        d.setCapacidade(capacidade);
        d.setStatus(status);
        return d;
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("smoke: cadastra mesa normalmente com id Long")
        void smokeTest() {
            MesaResponseDTO r = mesaService.create(dto(10, 4, "LIVRE"));

            assertNotNull(r.getId());
            assertEquals(10, r.getNumero());
            assertEquals(4, r.getCapacidade());
            assertEquals("LIVRE", r.getStatus());
            assertNotNull(r.getCreated_at());
            assertEquals(1, mesaRepository.count());
        }

        @Test
        @DisplayName("deve persistir o registro no banco")
        void persisteNoBanco() {
            MesaResponseDTO r = mesaService.create(dto(10, 4, "LIVRE"));

            assertTrue(mesaRepository.findById(r.getId()).isPresent());
        }

        @Test
        @DisplayName("deve permitir cadastrar mesas diferentes")
        void mesasDiferentes() {
            MesaResponseDTO primeira = mesaService.create(dto(10, 4, "LIVRE"));
            MesaResponseDTO segunda = mesaService.create(dto(11, 2, "LIVRE"));

            assertNotEquals(primeira.getId(), segunda.getId());
            assertEquals(2, mesaRepository.count());
        }

        @Test
        @DisplayName("numero duplicado deve dar 409 CONFLICT")
        void numeroDuplicado() {
            mesaService.create(dto(10, 4, "LIVRE"));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> mesaService.create(dto(10, 2, "LIVRE")));
            assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
            assertEquals(1, mesaRepository.count());
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("findById existente")
        void findByIdExistente() {
            MesaResponseDTO criado = mesaService.create(dto(5, 2, "LIVRE"));
            MesaResponseDTO achado = mesaService.findById(criado.getId());

            assertEquals(criado.getId(), achado.getId());
            assertEquals(5, achado.getNumero());
        }

        @Test
        @DisplayName("findById inexistente lança EntityNotFoundException")
        void findByIdInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> mesaService.findById(999999L));
            assertTrue(ex.getMessage().contains("999999"));
        }

        @Test
        @DisplayName("findAll lista tudo")
        void findAllTest() {
            assertTrue(mesaService.findAll().isEmpty());
            mesaService.create(dto(1, 2, "LIVRE"));
            mesaService.create(dto(2, 4, "OCUPADA"));

            List<MesaResponseDTO> todas = mesaService.findAll();
            assertEquals(2, todas.size());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("update altera e persiste, sem criar outro registro")
        void updateTest() {
            MesaResponseDTO criado = mesaService.create(dto(7, 2, "LIVRE"));

            MesaResponseDTO atualizado = mesaService.update(criado.getId(), dto(7, 6, "OCUPADA"));

            assertEquals(criado.getId(), atualizado.getId());
            assertEquals(6, atualizado.getCapacidade());
            assertEquals("OCUPADA", atualizado.getStatus());
            assertEquals(1, mesaRepository.count());

            // relê do banco para provar que persistiu
            MesaResponseDTO doBanco = mesaService.findById(criado.getId());
            assertEquals(6, doBanco.getCapacidade());
            assertEquals("OCUPADA", doBanco.getStatus());
        }

        @Test
        @DisplayName("update não deve alterar o created_at")
        void updateMantemCreatedAt() {
            MesaResponseDTO criado = mesaService.create(dto(7, 2, "LIVRE"));
            var createdAtAntes = mesaService.findById(criado.getId()).getCreated_at();

            mesaService.update(criado.getId(), dto(7, 6, "OCUPADA"));

            var createdAtDepois = mesaService.findById(criado.getId()).getCreated_at();
            assertEquals(createdAtAntes, createdAtDepois);
        }

        @Test
        @DisplayName("deve permitir atualizar mantendo o próprio número")
        void atualizaMantendoMesmoNumero() {
            MesaResponseDTO criado = mesaService.create(dto(7, 2, "LIVRE"));

            MesaResponseDTO atualizado = mesaService.update(criado.getId(), dto(7, 8, "RESERVADA"));

            assertEquals(7, atualizado.getNumero());
            assertEquals(8, atualizado.getCapacidade());
            assertEquals("RESERVADA", atualizado.getStatus());
        }

        @Test
        @DisplayName("update para numero de outra mesa dá 409")
        void updateNumeroDuplicado() {
            mesaService.create(dto(1, 2, "LIVRE"));
            MesaResponseDTO segunda = mesaService.create(dto(2, 2, "LIVRE"));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> mesaService.update(segunda.getId(), dto(1, 2, "LIVRE")));
            assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());

            // garante que o registro original ficou inalterado
            MesaResponseDTO original = mesaService.findById(segunda.getId());
            assertEquals(2, original.getNumero());
        }

        @Test
        @DisplayName("update inexistente lança EntityNotFoundException e não cria registro")
        void updateInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> mesaService.update(999999L, dto(1, 2, "LIVRE")));

            assertTrue(ex.getMessage().contains("999999"));
            assertEquals(0, mesaRepository.count());
        }

        @Test
        @DisplayName("update não deve afetar as outras mesas")
        void updateNaoAfetaOutros() {
            MesaResponseDTO primeira = mesaService.create(dto(1, 2, "LIVRE"));
            MesaResponseDTO segunda = mesaService.create(dto(2, 4, "LIVRE"));

            mesaService.update(primeira.getId(), dto(1, 6, "OCUPADA"));

            MesaResponseDTO segundaDepois = mesaService.findById(segunda.getId());
            assertEquals(4, segundaDepois.getCapacidade());
            assertEquals("LIVRE", segundaDepois.getStatus());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("delete remove e segundo delete lança EntityNotFoundException")
        void deleteTest() {
            MesaResponseDTO criado = mesaService.create(dto(9, 4, "LIVRE"));
            mesaService.delete(criado.getId());

            assertEquals(0, mesaRepository.count());
            assertThrows(EntityNotFoundException.class,
                    () -> mesaService.delete(criado.getId()));
        }

        @Test
        @DisplayName("delete com id inexistente lança EntityNotFoundException")
        void deleteInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> mesaService.delete(999999L));

            assertTrue(ex.getMessage().contains("999999"));
        }

        @Test
        @DisplayName("delete deve remover apenas a mesa informada")
        void removeSomenteAInformada() {
            MesaResponseDTO primeira = mesaService.create(dto(1, 2, "LIVRE"));
            MesaResponseDTO segunda = mesaService.create(dto(2, 4, "LIVRE"));

            mesaService.delete(primeira.getId());

            assertEquals(1, mesaRepository.count());
            assertEquals(2, mesaService.findById(segunda.getId()).getNumero());
        }
    }
}
