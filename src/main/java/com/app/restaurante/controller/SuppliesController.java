package com.app.restaurante.controller;

import com.app.restaurante.dto.SuppliesCreateRequestDTO;
import com.app.restaurante.dto.SuppliesResponseDTO;
import com.app.restaurante.service.SuppliesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplies")
@Tag(name = "Insumos (supplies)", description = "Estoque de insumos")
public class SuppliesController {

    private final SuppliesService suppliesService;

    public SuppliesController(SuppliesService suppliesService) {
        this.suppliesService = suppliesService;
    }

    @PostMapping
    @Operation(summary = "Criar insumo")
    public ResponseEntity<SuppliesResponseDTO> create(@Valid @RequestBody SuppliesCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suppliesService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar insumos")
    public ResponseEntity<List<SuppliesResponseDTO>> findAll() {
        return ResponseEntity.ok(suppliesService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar insumo por id")
    public ResponseEntity<SuppliesResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(suppliesService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar insumo")
    public ResponseEntity<SuppliesResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody SuppliesCreateRequestDTO dto) {
        return ResponseEntity.ok(suppliesService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir insumo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        suppliesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}