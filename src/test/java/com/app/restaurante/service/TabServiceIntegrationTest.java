package com.app.restaurante.service;

import com.app.restaurante.dto.TabCreateRequestDTO;
import com.app.restaurante.dto.TabResponseDTO;
import com.app.restaurante.entity.EmployeeEntity;
import com.app.restaurante.entity.TabStatus;
import com.app.restaurante.repository.EmployeeRepository;
import com.app.restaurante.repository.TabRepository;
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
 * Teste de integração do CRUD de comandas (tabs), no padrão MesaService.
 * H2 em memória; tabela `tables` semeada via src/test/resources/import.sql.
 */
@SpringBootTest
class TabServiceIntegrationTest {

    @Autowired
    private TabService tabService;

    @Autowired
    private TabRepository tabRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void limparBanco() {
        tabRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    private TabCreateRequestDTO dto(Integer tableId) {
        TabCreateRequestDTO d = new TabCreateRequestDTO();
        d.setTableId(tableId);
        return d;
    }

    private Long criarGarcom() {
        EmployeeEntity garcom = EmployeeEntity.builder()
                .userId(10)
                .positionId(2)
                .workModelId(3)
                .salary(new BigDecimal("3500.50"))
                .workingHours(new BigDecimal("40.00"))
                .build();
        return employeeRepository.save(garcom).getId();
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("smoke: abre comanda com defaults (aberto, zeros)")
        void smokeTest() {
            TabResponseDTO r = tabService.create(dto(1));

            assertNotNull(r.getId());
            assertEquals(1, r.getTableId());
            assertEquals(TabStatus.aberto, r.getStatus());
            assertEquals(0, r.getServiceCharge().compareTo(BigDecimal.ZERO));
            assertEquals(0, r.getDiscount().compareTo(BigDecimal.ZERO));
            assertEquals(0, r.getTotalAmount().compareTo(BigDecimal.ZERO));
            assertNotNull(r.getCreatedAt());
            assertEquals(1, tabRepository.count());
        }

        @Test
        @DisplayName("create com garçom, valores e status pago")
        void createCompleto() {
            Long garcomId = criarGarcom();

            TabCreateRequestDTO d = dto(2);
            d.setWaiterId(garcomId.intValue());
            d.setStatus(TabStatus.pago);
            d.setServiceCharge(new BigDecimal("10.00"));
            d.setDiscount(new BigDecimal("5.00"));
            d.setTotalAmount(new BigDecimal("105.00"));

            TabResponseDTO r = tabService.create(d);

            assertEquals(TabStatus.pago, r.getStatus());
            assertEquals(garcomId.intValue(), r.getWaiterId());
            assertEquals(0, r.getTotalAmount().compareTo(new BigDecimal("105.00")));
        }

        @Test
        @DisplayName("table inexistente lança EntityNotFoundException")
        void tableInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> tabService.create(dto(999999)));
            assertTrue(ex.getMessage().contains("999999"));
            assertEquals(0, tabRepository.count());
        }

        @Test
        @DisplayName("garçom inexistente lança EntityNotFoundException")
        void garcomInexistente() {
            TabCreateRequestDTO d = dto(1);
            d.setWaiterId(999999);

            assertThrows(EntityNotFoundException.class, () -> tabService.create(d));
            assertEquals(0, tabRepository.count());
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("findById existente")
        void findByIdExistente() {
            TabResponseDTO criado = tabService.create(dto(1));
            TabResponseDTO achado = tabService.findById(criado.getId());

            assertEquals(criado.getId(), achado.getId());
            assertEquals(1, achado.getTableId());
        }

        @Test
        @DisplayName("findById inexistente lança EntityNotFoundException")
        void findByIdInexistente() {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> tabService.findById(999999));
            assertTrue(ex.getMessage().contains("999999"));
        }

        @Test
        @DisplayName("findAll lista tudo")
        void findAllTest() {
            assertTrue(tabService.findAll().isEmpty());
            tabService.create(dto(1));
            tabService.create(dto(2));

            List<TabResponseDTO> todas = tabService.findAll();
            assertEquals(2, todas.size());
        }
    }

    @Nested
    @DisplayName("update/delete")
    class UpdateDelete {

        @Test
        @DisplayName("update fecha a comanda e persiste valores")
        void updateTest() {
            TabResponseDTO criado = tabService.create(dto(1));

            TabCreateRequestDTO d = dto(1);
            d.setStatus(TabStatus.pago);
            d.setTotalAmount(new BigDecimal("87.50"));

            TabResponseDTO atualizado = tabService.update(criado.getId(), d);

            assertEquals(criado.getId(), atualizado.getId());
            assertEquals(TabStatus.pago, atualizado.getStatus());
            assertEquals(0, atualizado.getTotalAmount().compareTo(new BigDecimal("87.50")));
            assertEquals(1, tabRepository.count());
        }

        @Test
        @DisplayName("update inexistente lança EntityNotFoundException")
        void updateInexistente() {
            assertThrows(EntityNotFoundException.class,
                    () -> tabService.update(999999, dto(1)));
        }

        @Test
        @DisplayName("delete remove e segundo delete lança EntityNotFoundException")
        void deleteTest() {
            TabResponseDTO criado = tabService.create(dto(1));
            tabService.delete(criado.getId());

            assertEquals(0, tabRepository.count());
            assertThrows(EntityNotFoundException.class,
                    () -> tabService.delete(criado.getId()));
        }
    }
}
