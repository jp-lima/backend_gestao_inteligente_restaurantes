package com.app.restaurante.controller;

import com.app.restaurante.dto.PositionCreateRequestDTO;
import com.app.restaurante.dto.PositionResponseDTO;
import com.app.restaurante.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.util.List;

@RestController
@RequestMapping("/positions")
@Tag(name = "Cargos (positions)", description = "Cargos dos funcionários")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar cargo (só admin)")
    public ResponseEntity<PositionResponseDTO> create(@Valid @RequestBody PositionCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar cargos")
    public ResponseEntity<List<PositionResponseDTO>> findAll() {
        return ResponseEntity.ok(positionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cargo por id")
    public ResponseEntity<PositionResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(positionService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar cargo (só admin)")
    public ResponseEntity<PositionResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody PositionCreateRequestDTO dto) {
        return ResponseEntity.ok(positionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir cargo (só admin)")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        positionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
