package com.app.restaurante.service;

import com.app.restaurante.dto.SuppliesCreateRequestDTO;
import com.app.restaurante.dto.SuppliesResponseDTO;
import com.app.restaurante.entity.SuppliesEntity;
import com.app.restaurante.mapper.SuppliesMapper;
import com.app.restaurante.repository.SuppliesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuppliesService {

    private final SuppliesRepository suppliesRepository;
    private final SuppliesMapper suppliesMapper;

    public SuppliesService(SuppliesRepository suppliesRepository, SuppliesMapper suppliesMapper) {
        this.suppliesRepository = suppliesRepository;
        this.suppliesMapper = suppliesMapper;
    }

    public SuppliesResponseDTO create(SuppliesCreateRequestDTO dto) {
        SuppliesEntity entity = suppliesMapper.toEntity(dto);
        SuppliesEntity saved = suppliesRepository.save(entity);
        return suppliesMapper.toResponseDTO(saved);
    }

    public List<SuppliesResponseDTO> findAll() {
        return suppliesRepository.findAll()
                .stream()
                .map(suppliesMapper::toResponseDTO)
                .toList();
    }

    public SuppliesResponseDTO findById(Long id) {
        SuppliesEntity entity = suppliesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado para o ID: " + id));

        return suppliesMapper.toResponseDTO(entity);
    }

    public SuppliesResponseDTO update(Long id, SuppliesCreateRequestDTO dto) {
        SuppliesEntity entity = suppliesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado para o ID: " + id));

        suppliesMapper.updateEntityFromDTO(dto, entity);

        SuppliesEntity updated = suppliesRepository.save(entity);
        return suppliesMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        if (!suppliesRepository.existsById(id)) {
            throw new EntityNotFoundException("Insumo não encontrado para o ID: " + id);
        }
        suppliesRepository.deleteById(id);
    }
}