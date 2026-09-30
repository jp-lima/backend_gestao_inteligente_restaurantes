package com.app.restaurante.mapper;

import com.app.restaurante.dto.SuppliesCreateRequestDTO;
import com.app.restaurante.dto.SuppliesResponseDTO;
import com.app.restaurante.entity.SuppliesEntity;
import org.springframework.stereotype.Component;

@Component
public class SuppliesMapper {

    public SuppliesEntity toEntity(SuppliesCreateRequestDTO dto) {
        SuppliesEntity entity = new SuppliesEntity();
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        entity.setMinimum_stock(dto.getMinimumStock());
        entity.setUnit(dto.getUnit());
        entity.setPerishable(dto.getPerishable());
        entity.setExpiration_date(dto.getExpiration_date());
        return entity;
    }

    public SuppliesResponseDTO toResponseDTO(SuppliesEntity entity) {
        SuppliesResponseDTO dto = new SuppliesResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setQuantity(entity.getQuantity());
        dto.setMinimumStock(entity.getMinimum_stock());
        dto.setUnit(entity.getUnit());
        dto.setPerishable(entity.isPerishable());
        dto.setExpiration_date(entity.getExpiration_date());
        dto.setUpdatedAt(entity.getUpdated_at());
        return dto;
    }

    public void updateEntityFromDTO(SuppliesCreateRequestDTO dto, SuppliesEntity entity) {
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        entity.setMinimum_stock(dto.getMinimumStock());
        entity.setUnit(dto.getUnit());
        entity.setPerishable(dto.getPerishable());
        entity.setExpiration_date(dto.getExpiration_date());
    }
}