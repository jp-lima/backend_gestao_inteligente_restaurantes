package com.app.restaurante.service;

import com.app.restaurante.dto.MesaCreateRequestDTO;
import com.app.restaurante.dto.MesaResponseDTO;
import com.app.restaurante.dto.MesaUpdateRequestDTO;
import com.app.restaurante.entity.MesaEntity;
import com.app.restaurante.mapper.MesaMapper;
import com.app.restaurante.repository.MesaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    // injeção via construtor (mesmo padrão do AuthService)
    public MesaService(MesaRepository mesaRepository, MesaMapper mesaMapper) {
        this.mesaRepository = mesaRepository;
        this.mesaMapper = mesaMapper;
    }

    public MesaResponseDTO create(MesaCreateRequestDTO dto) {
        if (mesaRepository.existsByNumero(dto.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma mesa com esse número");
        }
        MesaEntity mesa = mesaMapper.toEntity(dto);
        MesaEntity saved = mesaRepository.save(mesa);
        return mesaMapper.toResponseDTO(saved);
    }

    public List<MesaResponseDTO> findAll() {
        return mesaRepository.findAll()
                .stream()
                .map(mesaMapper::toResponseDTO)
                .toList();
    }

    public MesaResponseDTO findById(UUID id) {
        MesaEntity mesa = getByIdOrThrow(id);
        return mesaMapper.toResponseDTO(mesa);
    }

    public MesaResponseDTO update(UUID id, MesaUpdateRequestDTO dto) {
        MesaEntity mesa = getByIdOrThrow(id);
        if (!mesa.getNumero().equals(dto.getNumero()) && mesaRepository.existsByNumero(dto.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma mesa com esse número");
        }
        mesaMapper.updateEntity(mesa, dto);
        MesaEntity saved = mesaRepository.save(mesa);
        return mesaMapper.toResponseDTO(saved);
    }

    public void delete(UUID id) {
        MesaEntity mesa = getByIdOrThrow(id);
        mesaRepository.delete(mesa);
    }

    private MesaEntity getByIdOrThrow(UUID id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));
    }
}
