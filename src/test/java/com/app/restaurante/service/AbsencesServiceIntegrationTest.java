package com.app.restaurante.service;

import com.app.restaurante.dto.AbsencesCreateRequestDTO;
import com.app.restaurante.dto.AbsencesResponseDTO;
import com.app.restaurante.entity.AbsenceStatus;
import com.app.restaurante.repository.AbsencesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração do CRUD de Absences.
 * H2 em memória, sem @Transactional de propósito.
 */
@SpringBootTest
class AbsencesServiceIntegrationTest {

    @Autowired
    private AbsencesService absencesService;

    @Autowired
    private AbsencesRepository absencesRepository;

    private static final OffsetDateTime INICIO = OffsetDateTime.parse("2026-12-01T08:00:00-03:00");
    private static final OffsetDateTime TERMINO = OffsetDateTime.parse("2026-12-10T18:00:00-03:00");

    @BeforeEach
    void limparBanco() {
        absencesRepository.deleteAll();
    }

    private AbsencesCreateRequestDTO dto() {
        return dto(1L, "Férias", "Viagem em família");
    }

    private AbsencesCreateRequestDTO dto(Long employeeId, String reason, String description) {
        AbsencesCreateRequestDTO dto = new AbsencesCreateRequestDTO();
        dto.setEmployeeId(employeeId);
        dto.setReason(reason);
        dto.setDescription(description);
        dto.setStartAt(INICIO);
        dto.setEndAt(TERMINO);
        return dto;
    }

    // =====================================================
    // create
    // =====================================================
    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("smoke: cadastra ausência normalmente com status PEDIDO")
        void smokeTest() {
            AbsencesResponseDTO r = absencesService.create(dto());

            assertNotNull(r.getId());
            assertEquals(1L, r.getEmployeeId());
            assertEquals("Férias", r.getReason());
            assertEquals("Viagem em família", r.getDescription());
            assertEquals(AbsenceStatus.PEDIDO, r.getStatus());
            assertEquals(INICIO, r.getStartAt());
            assertEquals(TERMINO, r.getEndAt());
            assertNotNull(r.getCreatedAt());
        }

        @Test
        @DisplayName("deve persistir o registro no banco")
        void persisteNoBanco() {
            AbsencesResponseDTO r = absencesService.create(dto());

            assertEquals(1, absencesRepository.count());
            assertTrue(absencesRepository.findById(r.getId()).isPresent());
        }

        @Test
        @DisplayName("deve permitir cadastrar várias ausências")
        void variasAusencias() {
            AbsencesResponseDTO primeira = absencesService.create(dto());
            AbsencesResponseDTO segunda = absencesService.create(dto(2L, "Licença", "Atestado"));

            assertNotEquals(primeira.getId(), segunda.getId());
            assertEquals(2, absencesRepository.count());
        }

        /*
         * COMPORTAMENTO ATUAL: não há validação de employeeId existente nem de ordenação
         * entre startAt e endAt. Esse teste documenta que datas invertidas são aceitas hoje.
         */
        @Test
        @DisplayName("hoje startAt depois de endAt é aceito (sem validação de ordenação)")
        void datasInvertidas_hojePassam() {
            AbsencesCreateRequestDTO dto = dto();
            dto.setStartAt(TERMINO);
            dto.setEndAt(INICIO);

            AbsencesResponseDTO r = absencesService.create(dto);

            assertEquals(TERMINO, r.getStartAt());
            assertEquals(INICIO, r.getEndAt());
        }
    }

    // =====================================================
    // findById / findAll
    // =====================================================
    @Nested
    @DisplayName("findById e findAll")
    class Consultas {

        @Test
        @DisplayName("findById existente")
        void findByIdExistente() {
            AbsencesResponseDTO criado = absencesService.create(dto());

            AbsencesResponseDTO achado = absencesService.findById(criado.getId());

            assertEquals(criado.getId(), achado.getId());
            assertEquals("Férias", achado.getReason());
        }

        @Test
        @DisplayName("findById inexistente lança EntityNotFoundException")
        void findByIdInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> absencesService.findById(999999L));

            assertTrue(ex.getMessage().contains("999999"));
        }

        @Test
        @DisplayName("findAll lista vazia quando não há ausências")
        void findAllVazio() {
            assertTrue(absencesService.findAll().isEmpty());
        }

        @Test
        @DisplayName("findAll retorna todas as ausências cadastradas")
        void findAllComRegistros() {
            absencesService.create(dto());
            absencesService.create(dto(2L, "Licença", "Atestado"));

            List<AbsencesResponseDTO> todas = absencesService.findAll();

            assertEquals(2, todas.size());
        }
    }

    // =====================================================
    // update
    // =====================================================
    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("deve alterar e persistir sem criar outro registro")
        void updateTest() {
            AbsencesResponseDTO criado = absencesService.create(dto());

            AbsencesCreateRequestDTO novo = dto(99L, "Licença médica", "Atestado enviado");
            novo.setStartAt(INICIO.plusDays(1));
            novo.setEndAt(TERMINO.plusDays(1));

            AbsencesResponseDTO atualizado = absencesService.update(criado.getId(), novo);

            assertEquals(criado.getId(), atualizado.getId());
            assertEquals("Licença médica", atualizado.getReason());
            assertEquals("Atestado enviado", atualizado.getDescription());
            assertEquals(INICIO.plusDays(1), atualizado.getStartAt());
            assertEquals(TERMINO.plusDays(1), atualizado.getEndAt());
            assertEquals(1, absencesRepository.count());

            // relê do banco
            AbsencesResponseDTO doBanco = absencesService.findById(criado.getId());
            assertEquals("Licença médica", doBanco.getReason());
        }

        @Test
        @DisplayName("update deve manter employeeId e status originais")
        void updateMantemEmployeeIdEStatus() {
            AbsencesResponseDTO criado = absencesService.create(dto());
            absencesService.approve(criado.getId());

            AbsencesCreateRequestDTO novo = dto(99L, "Outro motivo", "Outra descrição");
            absencesService.update(criado.getId(), novo);

            AbsencesResponseDTO atualizado = absencesService.findById(criado.getId());
            assertEquals(1L, atualizado.getEmployeeId());
            assertEquals(AbsenceStatus.APROVADO, atualizado.getStatus());
        }

        @Test
        @DisplayName("update inexistente lança EntityNotFoundException e não cria registro")
        void updateInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> absencesService.update(999999L, dto()));

            assertTrue(ex.getMessage().contains("999999"));
            assertEquals(0, absencesRepository.count());
        }

        @Test
        @DisplayName("update não deve afetar as outras ausências")
        void updateNaoAfetaOutros() {
            AbsencesResponseDTO primeira = absencesService.create(dto());
            AbsencesResponseDTO segunda = absencesService.create(dto(2L, "Licença", "Atestado"));

            AbsencesCreateRequestDTO novo = dto(1L, "Motivo alterado", "Descrição alterada");
            absencesService.update(primeira.getId(), novo);

            AbsencesResponseDTO segundaDepois = absencesService.findById(segunda.getId());
            assertEquals("Licença", segundaDepois.getReason());
            assertEquals("Atestado", segundaDepois.getDescription());
        }
    }

    // =====================================================
    // approve / reject
    // =====================================================
    @Nested
    @DisplayName("approve e reject")
    class Aprovacao {

        @Test
        @DisplayName("approve deve alterar o status para APROVADO")
        void approveTest() {
            AbsencesResponseDTO criado = absencesService.create(dto());

            AbsencesResponseDTO aprovado = absencesService.approve(criado.getId());

            assertEquals(AbsenceStatus.APROVADO, aprovado.getStatus());
            assertEquals(AbsenceStatus.APROVADO, absencesService.findById(criado.getId()).getStatus());
        }

        @Test
        @DisplayName("reject deve alterar o status para REPROVADO")
        void rejectTest() {
            AbsencesResponseDTO criado = absencesService.create(dto());

            AbsencesResponseDTO reprovado = absencesService.reject(criado.getId());

            assertEquals(AbsenceStatus.REPROVADO, reprovado.getStatus());
            assertEquals(AbsenceStatus.REPROVADO, absencesService.findById(criado.getId()).getStatus());
        }

        @Test
        @DisplayName("approve inexistente lança EntityNotFoundException")
        void approveInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> absencesService.approve(999999L));

            assertTrue(ex.getMessage().contains("999999"));
        }

        @Test
        @DisplayName("reject inexistente lança EntityNotFoundException")
        void rejectInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> absencesService.reject(999999L));

            assertTrue(ex.getMessage().contains("999999"));
        }
    }

    // =====================================================
    // delete
    // =====================================================
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deve remover a ausência do banco")
        void removeAusencia() {
            AbsencesResponseDTO criado = absencesService.create(dto());

            absencesService.delete(criado.getId());

            assertEquals(0, absencesRepository.count());
            assertThrows(EntityNotFoundException.class,
                    () -> absencesService.findById(criado.getId()));
        }

        @Test
        @DisplayName("delete com id inexistente lança EntityNotFoundException")
        void deleteInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> absencesService.delete(999999L));

            assertTrue(ex.getMessage().contains("999999"));
        }

        @Test
        @DisplayName("delete deve remover apenas a ausência informada")
        void removeSomenteAInformada() {
            AbsencesResponseDTO primeira = absencesService.create(dto());
            AbsencesResponseDTO segunda = absencesService.create(dto(2L, "Licença", "Atestado"));

            absencesService.delete(primeira.getId());

            assertEquals(1, absencesRepository.count());
            assertEquals("Licença", absencesService.findById(segunda.getId()).getReason());
        }
    }
}
