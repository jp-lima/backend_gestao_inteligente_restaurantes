package com.app.restaurante.controller;

import com.app.restaurante.dto.MesaCreateRequestDTO;
import com.app.restaurante.dto.MesaResponseDTO;
import com.app.restaurante.service.MesaService;
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
import java.util.List;

@RestController
@RequestMapping("/mesas")
@Tag(name = "Mesas", description = "Gestão de mesas")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @PostMapping
    @Operation(summary = "Criar mesa")
    public ResponseEntity<MesaResponseDTO> create(@Valid @RequestBody MesaCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar mesas")
    public ResponseEntity<List<MesaResponseDTO>> findAll() {
        return ResponseEntity.ok(mesaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar mesa por id")
    public ResponseEntity<MesaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar mesa")
    public ResponseEntity<MesaResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MesaCreateRequestDTO dto) {
        return ResponseEntity.ok(mesaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir mesa")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mesaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
