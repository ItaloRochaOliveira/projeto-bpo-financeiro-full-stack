package com.italo.geradorboleto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustoDetalheResponse {
    
    private String id;
    private String descricao;
    private BigDecimal valor;
    private String tipoCusto;
    private BigDecimal porcentagem;
}
