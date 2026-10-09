package com.app.restaurante.mapper;

import com.app.restaurante.dto.EmployeeCreateRequestDTO;
import com.app.restaurante.dto.EmployeeResponseDTO;
import com.app.restaurante.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeEntity toEntity(EmployeeCreateRequestDTO dto) {
        EmployeeEntity entity = new EmployeeEntity();
        updateEntityFromDTO(dto, entity);
        return entity;
    }

    public EmployeeResponseDTO toResponseDTO(EmployeeEntity entity) {
        return new EmployeeResponseDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getPositionId(),
                entity.getWorkModelId(),
                entity.getSalary(),
                entity.getWorkingHours(),
                entity.getCreatedAt()
        );
    }

    public void updateEntityFromDTO(EmployeeCreateRequestDTO dto, EmployeeEntity entity) {
        entity.setUserId(dto.getUserId());
        entity.setPositionId(dto.getPositionId());
        entity.setWorkModelId(dto.getWorkModelId());
        entity.setSalary(dto.getSalary());
        entity.setWorkingHours(dto.getWorkingHours());
    }
}
