package com.app.restaurante.mapper;
import com.app.restaurante.dto.WorkModelsCreateRequestDTO;
import com.app.restaurante.dto.WorkModelsResponseDTO;
import com.app.restaurante.entity.WorkModelsEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkModelsMapper {
    public WorkModelsEntity toEntity(WorkModelsCreateRequestDTO dto) {
        WorkModelsEntity entity = new WorkModelsEntity();

        entity.setType(dto.getType());

        return entity;
    }

    public WorkModelsResponseDTO toResponseDTO(WorkModelsEntity entity) {
        WorkModelsResponseDTO dto = new WorkModelsResponseDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        return dto;
    }

    public void updateEntityFromDTO(WorkModelsCreateRequestDTO dto, WorkModelsEntity entity) {
        entity.setType(dto.getType());
    }
}