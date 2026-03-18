package com.italo.geradorboleto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PrecoResponse {
    
    private String id;
    private String equipamento;
    private BigDecimal investimento;
    private BigDecimal residual;
    private Integer depreciacaoMeses;
    private BigDecimal precoAtualMensal;
    private BigDecimal margem;
    private BigDecimal manutencaoAtual;
    private String faturamentoId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private LocalDateTime deletedAt;
    private String userId;
}
