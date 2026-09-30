package com.app.restaurante.controller;

import com.app.restaurante.dto.PratoCreateRequestDTO;
import com.app.restaurante.dto.PratoResponseDTO;
import com.app.restaurante.service.PratoService;
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
@RequestMapping("/pratos")
public class PratosController {

    private final PratoService pratoService;

    public PratosController(PratoService pratoService) {
        this.pratoService = pratoService;
    }

    @GetMapping
    public ResponseEntity<List<PratoResponseDTO>> findAll() {
        return ResponseEntity.ok(pratoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PratoResponseDTO> findById(@PathVariable Integer id) {
        return pratoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PratoResponseDTO> create(@Valid @RequestBody PratoCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pratoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PratoResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody PratoCreateRequestDTO dto) {
        return pratoService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return pratoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
