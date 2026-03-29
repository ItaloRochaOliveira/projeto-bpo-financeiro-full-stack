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
    private BigDecimal manutencaoAnual;
    private BigDecimal qtde;
    private BigDecimal taxaOcupacao;
    private BigDecimal mediaAlugados;
    private BigDecimal rateio;
    private BigDecimal custoRateado;
    private BigDecimal manutencaoMensal;
    private BigDecimal custo;
    private BigDecimal depreciacao;
    private BigDecimal lucro;
    private BigDecimal pontoEquilibrio;
    private BigDecimal precoAdequado;
    private BigDecimal precoAtualMenosPrecoAdequado;
    private BigDecimal faturamentoEstimado;
    private BigDecimal resultado;
    private BigDecimal lucroTotal;
    private BigDecimal paybackMeses;
}
