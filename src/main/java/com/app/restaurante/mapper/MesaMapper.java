package com.app.restaurante.mapper;

import com.app.restaurante.dto.MesaCreateRequestDTO;
import com.app.restaurante.dto.MesaResponseDTO;
import com.app.restaurante.dto.MesaUpdateRequestDTO;
import com.app.restaurante.entity.MesaEntity;
import org.springframework.stereotype.Component;

@Component
public class MesaMapper {

    public MesaEntity toEntity(MesaCreateRequestDTO dto) {
        MesaEntity mesa = new MesaEntity();
        mesa.setNumero(dto.getNumero());
        mesa.setCapacidade(dto.getCapacidade());
        mesa.setStatus(dto.getStatus());
        return mesa;
    }

    public void updateEntity(MesaEntity mesa, MesaUpdateRequestDTO dto) {
        mesa.setNumero(dto.getNumero());
        mesa.setCapacidade(dto.getCapacidade());
        mesa.setStatus(dto.getStatus());
    }

    public MesaResponseDTO toResponseDTO(MesaEntity entity) {
        MesaResponseDTO dto = new MesaResponseDTO();
        dto.setId(entity.getId());
        dto.setNumero(entity.getNumero());
        dto.setCapacidade(entity.getCapacidade());
        dto.setStatus(entity.getStatus());
        dto.setCreated_at(entity.getCreated_at());
        dto.setUpdated_at(entity.getUpdated_at());
        return dto;
    }
}
