package com.app.restaurante.mapper;

import com.app.restaurante.dto.MesaCreateRequestDTO;
import com.app.restaurante.dto.MesaResponseDTO;
import com.app.restaurante.entity.MesaEntity;
import org.springframework.stereotype.Component;

@Component
public class MesaMapper {

    public MesaEntity toEntity(MesaCreateRequestDTO dto) {
        MesaEntity mesa = new MesaEntity();
        mesa.setNumber(dto.getNumber());
        mesa.setSeating_capacity(dto.getSeating_capacity());
        mesa.setAvailable(dto.getAvailable());
        return mesa;
    }

    public void updateEntityFromDTO(MesaCreateRequestDTO dto, MesaEntity entity) {
        entity.setNumber(dto.getNumber());
        entity.setSeating_capacity(dto.getSeating_capacity());
        entity.setAvailable(dto.getAvailable());
    }

    public MesaResponseDTO toResponseDTO(MesaEntity entity) {
        MesaResponseDTO dto = new MesaResponseDTO();
        dto.setId(entity.getId());
        dto.setNumber(entity.getNumber());
        dto.setSeating_capacity(entity.getSeating_capacity());
        dto.setAvailable(entity.getAvailable());
        dto.setCreated_at(entity.getCreated_at());
        return dto;
    }
}
