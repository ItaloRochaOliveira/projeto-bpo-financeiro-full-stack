package com.italo.geradorboleto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FaturamentoResponse {
    
    private String id;
    private String equipamento;
    private BigDecimal mediaAlugados;
    private BigDecimal porcentagem;
}
