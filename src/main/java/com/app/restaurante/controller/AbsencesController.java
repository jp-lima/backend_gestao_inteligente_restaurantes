package com.app.restaurante.controller;

import com.app.restaurante.dto.AbsencesCreateRequestDTO;
import com.app.restaurante.dto.AbsencesResponseDTO;
import com.app.restaurante.service.AbsencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/absences")
@Tag(name = "Ausências", description = "Gestão de ausências de funcionários")
public class AbsencesController {

    private final AbsencesService absencesService;

    public AbsencesController(AbsencesService absencesService) {
        this.absencesService = absencesService;
    }

    @PostMapping
    @Operation(summary = "Criar ausência")
    public ResponseEntity<AbsencesResponseDTO> create(@Valid @RequestBody AbsencesCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(absencesService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar ausências")
    public ResponseEntity<List<AbsencesResponseDTO>> findAll() {
        return ResponseEntity.ok(absencesService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ausência por id")
    public ResponseEntity<AbsencesResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(absencesService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ausência")
    public ResponseEntity<AbsencesResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody AbsencesCreateRequestDTO dto) {
        return ResponseEntity.ok(absencesService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir ausência")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        absencesService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Aprovar ausência")
    public ResponseEntity<AbsencesResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(absencesService.approve(id));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Rejeitar ausência")
    public ResponseEntity<AbsencesResponseDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(absencesService.reject(id));
    }
}