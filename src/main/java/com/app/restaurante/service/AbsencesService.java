package com.app.restaurante.service;

import com.app.restaurante.dto.AbsencesCreateRequestDTO;
import com.app.restaurante.dto.AbsencesResponseDTO;
import com.app.restaurante.entity.AbsenceStatus;
import com.app.restaurante.entity.AbsencesEntity;
import com.app.restaurante.mapper.AbsencesMapper;
import com.app.restaurante.repository.AbsencesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbsencesService {

    private final AbsencesRepository absencesRepository;
    private final AbsencesMapper absencesMapper;

    public AbsencesService(AbsencesRepository absencesRepository, AbsencesMapper absencesMapper) {
        this.absencesRepository = absencesRepository;
        this.absencesMapper = absencesMapper;
    }

    public AbsencesResponseDTO create(AbsencesCreateRequestDTO dto) {
        AbsencesEntity entity = absencesMapper.toEntity(dto);
        entity.setStatus(AbsenceStatus.PEDIDO);
        AbsencesEntity saved = absencesRepository.save(entity);
        return absencesMapper.toResponseDTO(saved);
    }

    public List<AbsencesResponseDTO> findAll() {
        return absencesRepository.findAll()
                .stream()
                .map(absencesMapper::toResponseDTO)
                .toList();
    }

    public AbsencesResponseDTO findById(Long id) {
        AbsencesEntity entity = absencesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ausência não encontrada para o ID: " + id));

        return absencesMapper.toResponseDTO(entity);
    }

    public AbsencesResponseDTO update(Long id, AbsencesCreateRequestDTO dto) {
        AbsencesEntity entity = absencesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ausência não encontrada para o ID: " + id));

        absencesMapper.updateEntityFromDTO(dto, entity);

        AbsencesEntity updated = absencesRepository.save(entity);
        return absencesMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        if (!absencesRepository.existsById(id)) {
            throw new EntityNotFoundException("Ausência não encontrada para o ID: " + id);
        }
        absencesRepository.deleteById(id);
    }

    public AbsencesResponseDTO approve(Long id) {
        AbsencesEntity entity = absencesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ausência não encontrada para o ID: " + id));

        entity.setStatus(AbsenceStatus.APROVADO);
        AbsencesEntity updated = absencesRepository.save(entity);
        return absencesMapper.toResponseDTO(updated);
    }

    public AbsencesResponseDTO reject(Long id) {
        AbsencesEntity entity = absencesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ausência não encontrada para o ID: " + id));

        entity.setStatus(AbsenceStatus.REPROVADO);
        AbsencesEntity updated = absencesRepository.save(entity);
        return absencesMapper.toResponseDTO(updated);
    }
}