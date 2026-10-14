package com.app.restaurante.service;

import com.app.restaurante.dto.MesaCreateRequestDTO;
import com.app.restaurante.dto.MesaResponseDTO;
import com.app.restaurante.entity.MesaEntity;
import com.app.restaurante.mapper.MesaMapper;
import com.app.restaurante.repository.MesaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    public MesaService(MesaRepository mesaRepository, MesaMapper mesaMapper) {
        this.mesaRepository = mesaRepository;
        this.mesaMapper = mesaMapper;
    }

    public MesaResponseDTO create(MesaCreateRequestDTO dto) {
        if (mesaRepository.existsByNumero(dto.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma mesa com esse número");
        }
        MesaEntity saved = mesaRepository.save(mesaMapper.toEntity(dto));
        return mesaMapper.toResponseDTO(saved);
    }

    public List<MesaResponseDTO> findAll() {
        return mesaRepository.findAll()
                .stream()
                .map(mesaMapper::toResponseDTO)
                .toList();
    }

    public MesaResponseDTO findById(Long id) {
        return mesaRepository.findById(id)
                .map(mesaMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada pelo ID: " + id));
    }

    public MesaResponseDTO update(Long id, MesaCreateRequestDTO dto) {
        MesaEntity entity = mesaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada pelo ID: " + id));

        if (!entity.getNumero().equals(dto.getNumero()) && mesaRepository.existsByNumero(dto.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma mesa com esse número");
        }
        mesaMapper.updateEntityFromDTO(dto, entity);
        return mesaMapper.toResponseDTO(mesaRepository.save(entity));
    }

    public void delete(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new EntityNotFoundException("Mesa não encontrada pelo ID: " + id);
        }
        mesaRepository.deleteById(id);
    }
}
