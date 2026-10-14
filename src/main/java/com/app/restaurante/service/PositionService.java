package com.app.restaurante.service;

import com.app.restaurante.dto.PositionCreateRequestDTO;
import com.app.restaurante.dto.PositionResponseDTO;
import com.app.restaurante.entity.PositionEntity;
import com.app.restaurante.mapper.PositionMapper;
import com.app.restaurante.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public PositionService(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    public PositionResponseDTO create(PositionCreateRequestDTO dto) {
        PositionEntity saved = positionRepository.save(positionMapper.toEntity(dto));
        return positionMapper.toResponseDTO(saved);
    }

    public List<PositionResponseDTO> findAll() {
        return positionRepository.findAll()
                .stream()
                .map(positionMapper::toResponseDTO)
                .toList();
    }

    public PositionResponseDTO findById(Integer id) {
        return positionRepository.findById(id)
                .map(positionMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado pelo ID: " + id));
    }

    public PositionResponseDTO update(Integer id, PositionCreateRequestDTO dto) {
        PositionEntity entity = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado pelo ID: " + id));

        positionMapper.updateEntityFromDTO(dto, entity);
        return positionMapper.toResponseDTO(positionRepository.save(entity));
    }

    public void delete(Integer id) {
        if (!positionRepository.existsById(id)) {
            throw new EntityNotFoundException("Cargo não encontrado pelo ID: " + id);
        }
        positionRepository.deleteById(id);
    }
}
