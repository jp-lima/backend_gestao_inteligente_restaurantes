package com.app.restaurante.mapper;

import com.app.restaurante.dto.TabCreateRequestDTO;
import com.app.restaurante.dto.TabResponseDTO;
import com.app.restaurante.entity.TabEntity;
import com.app.restaurante.entity.TabStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TabMapper {

    public TabEntity toEntity(TabCreateRequestDTO dto) {
        TabEntity tab = new TabEntity();
        updateEntityFromDTO(dto, tab);
        return tab;
    }

    public void updateEntityFromDTO(TabCreateRequestDTO dto, TabEntity entity) {
        entity.setTableId(dto.getTableId());
        entity.setWaiterId(dto.getWaiterId());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : TabStatus.aberto);
        entity.setServiceCharge(dto.getServiceCharge() != null ? dto.getServiceCharge() : BigDecimal.ZERO);
        entity.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        entity.setTotalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO);
        entity.setPaymentTime(dto.getPaymentTime());
    }

    public TabResponseDTO toResponseDTO(TabEntity entity) {
        TabResponseDTO dto = new TabResponseDTO();
        dto.setId(entity.getId());
        dto.setTableId(entity.getTableId());
        dto.setWaiterId(entity.getWaiterId());
        dto.setStatus(entity.getStatus());
        dto.setServiceCharge(entity.getServiceCharge());
        dto.setDiscount(entity.getDiscount());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaymentTime(entity.getPaymentTime());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
