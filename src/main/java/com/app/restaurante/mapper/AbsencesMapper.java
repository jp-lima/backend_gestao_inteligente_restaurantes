package com.app.restaurante.mapper;
import com.app.restaurante.dto.AbsencesCreateRequestDTO;
import com.app.restaurante.dto.AbsencesResponseDTO;
import com.app.restaurante.entity.AbsencesEntity;
import org.springframework.stereotype.Component;

@Component
public class AbsencesMapper {
    public AbsencesEntity toEntity (AbsencesCreateRequestDTO dto) {
        AbsencesEntity entity = new AbsencesEntity();

        entity.setEmployeeId(dto.getEmployeeId());
        entity.setReason(dto.getReason());
        entity.setDescription(dto.getDescription());
        entity.setStartAt(dto.getStartAt());
        entity.setEndAt(dto.getEndAt());

        return entity;
    }

    public AbsencesResponseDTO toResponseDTO(AbsencesEntity entity){
        AbsencesResponseDTO dto = new AbsencesResponseDTO();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setReason(entity.getReason());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setStartAt(entity.getStartAt());
        dto.setEndAt(entity.getEndAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return  dto;
    }

    public void updateEntityFromDTO(AbsencesCreateRequestDTO dto, AbsencesEntity entity) {
        entity.setReason(dto.getReason());
        entity.setDescription(dto.getDescription());
        entity.setStartAt(dto.getStartAt());
        entity.setEndAt(dto.getEndAt());
    }
}
