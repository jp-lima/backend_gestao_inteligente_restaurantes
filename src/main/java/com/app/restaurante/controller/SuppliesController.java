package com.app.restaurante.controller;

import com.app.restaurante.dto.SuppliesCreateRequestDTO;
import com.app.restaurante.dto.SuppliesResponseDTO;
import com.app.restaurante.service.SuppliesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplies")
public class SuppliesController {

    private final SuppliesService suppliesService;

    public SuppliesController(SuppliesService suppliesService) {
        this.suppliesService = suppliesService;
    }

    @PostMapping
    public ResponseEntity<SuppliesResponseDTO> create(@Valid @RequestBody SuppliesCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suppliesService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SuppliesResponseDTO>> findAll() {
        return ResponseEntity.ok(suppliesService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuppliesResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(suppliesService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuppliesResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody SuppliesCreateRequestDTO dto) {
        return ResponseEntity.ok(suppliesService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        suppliesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}