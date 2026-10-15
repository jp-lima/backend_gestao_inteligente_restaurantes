package com.app.restaurante.controller;

import com.app.restaurante.dto.CategoriesCreateRequestDTO;
import com.app.restaurante.dto.CategoriesResponseDTO;
import com.app.restaurante.service.CategoriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categorias", description = "Categorias do cardápio")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar categoria (só admin)")
    public ResponseEntity<CategoriesResponseDTO> create(@Valid @RequestBody CategoriesCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriesService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar categorias")
    public ResponseEntity<List<CategoriesResponseDTO>> findAll() {
        return ResponseEntity.ok(categoriesService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por id")
    public ResponseEntity<CategoriesResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriesService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar categoria (só admin)")
    public ResponseEntity<CategoriesResponseDTO> update(@PathVariable Long id,
                                                        @Valid @RequestBody CategoriesCreateRequestDTO dto) {
        return ResponseEntity.ok(categoriesService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir categoria (só admin)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}