package com.italo.geradorboleto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FaturamentoResponse {
    
    private String id;
    private String equipamento;
    private BigDecimal totalEquipamento;
    private BigDecimal mediaAlugados;
    private BigDecimal porcentagem;
    private String precoId;
    private String precoEquipamento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private LocalDateTime deletedAt;
    private String userId;
}
