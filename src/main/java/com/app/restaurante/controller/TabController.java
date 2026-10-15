package com.app.restaurante.controller;

import com.app.restaurante.dto.TabCreateRequestDTO;
import com.app.restaurante.dto.TabResponseDTO;
import com.app.restaurante.service.TabService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/tabs")
@Tag(name = "Comandas (tabs)", description = "Gestão de comandas")
public class TabController {

    private final TabService tabService;

    public TabController(TabService tabService) {
        this.tabService = tabService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Abrir comanda (só admin)")
    public ResponseEntity<TabResponseDTO> create(@Valid @RequestBody TabCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tabService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar comandas")
    public ResponseEntity<List<TabResponseDTO>> findAll() {
        return ResponseEntity.ok(tabService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar comanda por id")
    public ResponseEntity<TabResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(tabService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar comanda (só admin)")
    public ResponseEntity<TabResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody TabCreateRequestDTO dto) {
        return ResponseEntity.ok(tabService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Fechar/excluir comanda (só admin)")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tabService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
