package com.italo.geradorboleto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResumoCustosResponse {
    
    private BigDecimal value;
    private Double porcentagem;
}
