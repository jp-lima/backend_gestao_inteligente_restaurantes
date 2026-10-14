package com.app.restaurante.mapper;

import com.app.restaurante.dto.PositionCreateRequestDTO;
import com.app.restaurante.dto.PositionResponseDTO;
import com.app.restaurante.entity.PositionEntity;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    public PositionEntity toEntity(PositionCreateRequestDTO dto) {
        PositionEntity entity = new PositionEntity();
        updateEntityFromDTO(dto, entity);
        return entity;
    }

    public PositionResponseDTO toResponseDTO(PositionEntity entity) {
        return new PositionResponseDTO(entity.getId(), entity.getType());
    }

    public void updateEntityFromDTO(PositionCreateRequestDTO dto, PositionEntity entity) {
        entity.setType(dto.getType());
    }
}
