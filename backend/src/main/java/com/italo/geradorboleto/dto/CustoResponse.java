package com.italo.geradorboleto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustoResponse {
    
    private String id;
    private String descricao;
    private BigDecimal valor;
    private String tipoCusto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private LocalDateTime deletedAt;
    private String userId;
}
