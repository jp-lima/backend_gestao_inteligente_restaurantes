package com.app.restaurante.dto;

import com.app.restaurante.entity.TabStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TabCreateRequestDTO {

    @NotNull(message = "O ID da table é obrigatório")
    @Positive(message = "O ID da table deve ser positivo")
    private Integer tableId;

    private UUID userId;

    @Positive(message = "O ID do garçom deve ser positivo")
    private Integer waiterId;

    private TabStatus status;

    @DecimalMin(value = "0.0", inclusive = true, message = "A taxa de serviço não pode ser negativa")
    private BigDecimal serviceCharge;

    @DecimalMin(value = "0.0", inclusive = true, message = "O desconto não pode ser negativo")
    private BigDecimal discount;

    @DecimalMin(value = "0.0", inclusive = true, message = "O total não pode ser negativo")
    private BigDecimal totalAmount;

    private OffsetDateTime paymentTime;
}
