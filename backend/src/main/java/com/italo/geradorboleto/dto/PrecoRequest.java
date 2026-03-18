package com.italo.geradorboleto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrecoRequest {
    
    @NotBlank(message = "Equipamento é obrigatório")
    private String equipamento;
    
    @NotNull(message = "Investimento é obrigatório")
    @DecimalMin(value = "0.01", message = "Investimento deve ser maior que zero")
    private BigDecimal investimento;
    
    @NotNull(message = "Valor residual é obrigatório")
    @DecimalMin(value = "0.00", message = "Valor residual não pode ser negativo")
    private BigDecimal residual;
    
    @NotNull(message = "Meses de depreciação é obrigatório")
    @Min(value = 1, message = "Meses de depreciação deve ser maior que zero")
    private Integer depreciacaoMeses;
    
    @NotNull(message = "Preço atual mensal é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço atual mensal deve ser maior que zero")
    private BigDecimal precoAtualMensal;
    
    @NotNull(message = "Margem é obrigatória")
    @DecimalMin(value = "0.00", message = "Margem não pode ser negativa")
    private BigDecimal margem;
    
    @NotNull(message = "Manutenção atual é obrigatória")
    @DecimalMin(value = "0.00", message = "Manutenção atual não pode ser negativa")
    private BigDecimal manutencaoAtual;
    
    private String faturamentoId;
}
