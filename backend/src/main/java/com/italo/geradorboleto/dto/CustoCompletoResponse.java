package com.italo.geradorboleto.dto;

import lombok.Data;

@Data
public class CustoCompletoResponse {
    
    private CustoDetalheResponse valueDB;
    private ResumoCompletoResponse resumo;
}
