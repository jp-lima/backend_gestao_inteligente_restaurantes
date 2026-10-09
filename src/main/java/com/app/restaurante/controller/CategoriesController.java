package com.app.restaurante.controller;

import com.app.restaurante.dto.CategoriesCreateRequestDTO;
import com.app.restaurante.dto.CategoriesResponseDTO;
import com.app.restaurante.service.CategoriesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PostMapping
    public ResponseEntity<CategoriesResponseDTO> create(@Valid @RequestBody CategoriesCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriesService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoriesResponseDTO>> findAll() {
        return ResponseEntity.ok(categoriesService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriesResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriesService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriesResponseDTO> update(@PathVariable Long id,
                                                        @Valid @RequestBody CategoriesCreateRequestDTO dto) {
        return ResponseEntity.ok(categoriesService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}