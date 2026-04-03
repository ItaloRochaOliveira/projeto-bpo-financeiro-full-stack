package com.italo.geradorboleto.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustosArrayResponse {
    
    private List<CustoSimplesResponse> data;
    private ResumoCompletoResponse resumo;
}
