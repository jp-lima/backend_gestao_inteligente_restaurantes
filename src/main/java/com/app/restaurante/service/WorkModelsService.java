package com.app.restaurante.service;

import com.app.restaurante.dto.WorkModelsCreateRequestDTO;
import com.app.restaurante.dto.WorkModelsResponseDTO;
import com.app.restaurante.entity.WorkModelsEntity;
import com.app.restaurante.mapper.WorkModelsMapper;
import com.app.restaurante.repository.WorkModelsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkModelsService {

    private final WorkModelsRepository workModelsRepository;
    private final WorkModelsMapper workModelsMapper;

    public WorkModelsService(WorkModelsRepository workModelsRepository, WorkModelsMapper workModelsMapper) {
        this.workModelsRepository = workModelsRepository;
        this.workModelsMapper = workModelsMapper;
    }

    public WorkModelsResponseDTO create(WorkModelsCreateRequestDTO dto) {
        WorkModelsEntity entity = workModelsMapper.toEntity(dto);
        WorkModelsEntity saved = workModelsRepository.save(entity);
        return workModelsMapper.toResponseDTO(saved);
    }

    public List<WorkModelsResponseDTO> findAll() {
        return workModelsRepository.findAll()
                .stream()
                .map(workModelsMapper::toResponseDTO)
                .toList();
    }

    public WorkModelsResponseDTO findById(Long id) {
        WorkModelsEntity entity = workModelsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modelo de trabalho não encontrado para o ID: " + id));

        return workModelsMapper.toResponseDTO(entity);
    }

    public WorkModelsResponseDTO update(Long id, WorkModelsCreateRequestDTO dto) {
        WorkModelsEntity entity = workModelsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modelo de trabalho não encontrado para o ID: " + id));

        workModelsMapper.updateEntityFromDTO(dto, entity);

        WorkModelsEntity updated = workModelsRepository.save(entity);
        return workModelsMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        if (!workModelsRepository.existsById(id)) {
            throw new EntityNotFoundException("Modelo de trabalho não encontrado para o ID: " + id);
        }
        workModelsRepository.deleteById(id);
    }
}