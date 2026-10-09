package com.app.restaurante.mapper;

import com.app.restaurante.dto.CategoriesCreateRequestDTO;
import com.app.restaurante.dto.CategoriesResponseDTO;
import com.app.restaurante.entity.CategoriesEntity;
import org.springframework.stereotype.Component;

@Component

public class CategoriesMapper  {
    public CategoriesEntity toEntity (CategoriesCreateRequestDTO dto) {
        CategoriesEntity entity = new CategoriesEntity();
        entity.setType(dto.getType());
        return entity;
    }

    public CategoriesResponseDTO toResponseDTO(CategoriesEntity entity){
        CategoriesResponseDTO dto = new CategoriesResponseDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        return  dto;
    }
    public void updateEntityFromDTO(CategoriesCreateRequestDTO dto, CategoriesEntity entity) {
        entity.setType(dto.getType());
    }
}

