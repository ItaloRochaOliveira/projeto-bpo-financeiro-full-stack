package com.italo.geradorboleto.dto;

import lombok.Data;

@Data
public class ResumoCompletoResponse {
    
    private ResumoCustosResponse custosFixos;
    private ResumoCustosResponse custosVariaveis;
    private ResumoCustosResponse total;
}
