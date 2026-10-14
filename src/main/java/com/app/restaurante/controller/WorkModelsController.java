package com.app.restaurante.controller;

import com.app.restaurante.dto.WorkModelsCreateRequestDTO;
import com.app.restaurante.dto.WorkModelsResponseDTO;
import com.app.restaurante.service.WorkModelsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-models")
public class WorkModelsController {

    private final WorkModelsService workModelsService;

    public WorkModelsController(WorkModelsService workModelsService) {
        this.workModelsService = workModelsService;
    }

    @PostMapping
    public ResponseEntity<WorkModelsResponseDTO> create(@Valid @RequestBody WorkModelsCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workModelsService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<WorkModelsResponseDTO>> findAll() {
        return ResponseEntity.ok(workModelsService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkModelsResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(workModelsService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkModelsResponseDTO> update(@PathVariable Long id,
                                                        @Valid @RequestBody WorkModelsCreateRequestDTO dto) {
        return ResponseEntity.ok(workModelsService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workModelsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}