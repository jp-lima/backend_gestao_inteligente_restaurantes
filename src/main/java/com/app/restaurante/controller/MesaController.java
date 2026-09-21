package com.app.restaurante.controller;

import com.app.restaurante.dto.MesaCreateRequestDTO;
import com.app.restaurante.dto.MesaResponseDTO;
import com.app.restaurante.dto.MesaUpdateRequestDTO;
import com.app.restaurante.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @PostMapping
    public ResponseEntity<MesaResponseDTO> create(@Valid @RequestBody MesaCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> findAll() {
        return ResponseEntity.ok(mesaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mesaService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> update(@PathVariable UUID id,
                                                  @Valid @RequestBody MesaUpdateRequestDTO dto) {
        return ResponseEntity.ok(mesaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mesaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
