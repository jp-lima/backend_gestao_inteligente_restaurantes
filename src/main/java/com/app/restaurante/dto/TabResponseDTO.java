package com.app.restaurante.dto;

import com.app.restaurante.entity.TabStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class TabResponseDTO {

    private Integer id;
    private Integer tableId;
    private Integer userId;
    private Integer waiterId;
    private TabStatus status;
    private BigDecimal serviceCharge;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private OffsetDateTime paymentTime;
    private OffsetDateTime createdAt;

    public TabResponseDTO() {
    }
}
