package com.app.restaurante.mapper;

import com.app.restaurante.dto.PratoCreateRequestDTO;
import com.app.restaurante.dto.PratoResponseDTO;
import com.app.restaurante.entity.CategoryEntity;
import com.app.restaurante.entity.PratoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PratoMapper {

    public PratoEntity toEntity(PratoCreateRequestDTO dto, CategoryEntity category) {
        PratoEntity prato = new PratoEntity();
        prato.setName(dto.getName());
        prato.setDescription(dto.getDescription());
        prato.setPrice(dto.getPrice());
        prato.setCategory(dto.getCategory());
        prato.setCategoryEntity(category);
        prato.setAvailable(dto.isAvailable());
        prato.setImageUrl(dto.getImageUrl());
        return prato;
    }

    public void updateEntity(PratoEntity prato, PratoCreateRequestDTO dto, CategoryEntity category) {
        prato.setName(dto.getName());
        prato.setDescription(dto.getDescription());
        prato.setPrice(dto.getPrice());
        prato.setCategory(dto.getCategory());
        prato.setCategoryEntity(category);
        prato.setAvailable(dto.isAvailable());
        prato.setImageUrl(dto.getImageUrl());
    }

    public PratoResponseDTO toResponseDTO(PratoEntity entity) {
        PratoResponseDTO dto = new PratoResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getName());
        dto.setDescricao(entity.getDescription());
        dto.setPreco(entity.getPrice());
        dto.setCategoria(entity.getCategory());
        dto.setCategoryId(entity.getCategoryEntity().getId());
        dto.setDisponivel(entity.isAvailable());
        dto.setImagemUrl(entity.getImageUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public List<PratoResponseDTO> toResponseDTOList(List<PratoEntity> entities) {
        return entities.stream().map(this::toResponseDTO).toList();
    }
}
