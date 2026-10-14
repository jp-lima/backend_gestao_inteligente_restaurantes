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
    @DisplayName("update/delete")
    class UpdateDelete {

        @Test
        @DisplayName("update altera e persiste")
        void updateTest() {
            MesaResponseDTO criado = mesaService.create(dto(7, 2, "LIVRE"));

            MesaResponseDTO atualizado = mesaService.update(criado.getId(), dto(7, 6, "OCUPADA"));

            assertEquals(criado.getId(), atualizado.getId());
            assertEquals(6, atualizado.getCapacidade());
            assertEquals("OCUPADA", atualizado.getStatus());
            assertEquals(1, mesaRepository.count());
        }

        @Test
        @DisplayName("update para numero de outra mesa dá 409")
        void updateNumeroDuplicado() {
            mesaService.create(dto(1, 2, "LIVRE"));
            MesaResponseDTO segunda = mesaService.create(dto(2, 2, "LIVRE"));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> mesaService.update(segunda.getId(), dto(1, 2, "LIVRE")));
            assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        }

        @Test
        @DisplayName("update inexistente lança EntityNotFoundException")
        void updateInexistente() {
            assertThrows(EntityNotFoundException.class,
                    () -> mesaService.update(999999L, dto(1, 2, "LIVRE")));
        }

        @Test
        @DisplayName("delete remove e segundo delete lança EntityNotFoundException")
        void deleteTest() {
            MesaResponseDTO criado = mesaService.create(dto(9, 4, "LIVRE"));
            mesaService.delete(criado.getId());

            assertEquals(0, mesaRepository.count());
            assertThrows(EntityNotFoundException.class,
                    () -> mesaService.delete(criado.getId()));
        }
    }
}
