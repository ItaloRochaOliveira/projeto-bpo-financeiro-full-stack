package com.italo.geradorboleto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FaturamentoRequest {
    
    @NotBlank(message = "Equipamento é obrigatório")
    private String equipamento;
    
    @NotNull(message = "Total do equipamento é obrigatório")
    @DecimalMin(value = "0.01", message = "Total do equipamento deve ser maior que zero")
    private BigDecimal totalEquipamento;
    
    @NotNull(message = "Média de alugados é obrigatória")
    @DecimalMin(value = "0.00", message = "Média de alugados não pode ser negativa")
    private BigDecimal mediaAlugados;
}
