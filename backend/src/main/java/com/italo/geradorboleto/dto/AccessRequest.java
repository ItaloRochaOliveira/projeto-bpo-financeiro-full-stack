package com.italo.geradorboleto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccessRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Motivo do acesso é obrigatório")
    private String accessReason;
    
    private String customReason;
    
    // Método para obter o motivo completo (pré-definido ou personalizado)
    public String getFullReason() {
        if ("OUTRO".equalsIgnoreCase(accessReason) && customReason != null && !customReason.trim().isEmpty()) {
            return customReason;
        }
        return accessReason;
    }

    public AccessRequest() {}

    public AccessRequest(String name, String email, String accessReason, String customReason) {
        this.name = name;
        this.email = email;
        this.accessReason = accessReason;
        this.customReason = customReason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessReason() {
        return accessReason;
    }

    public void setAccessReason(String accessReason) {
        this.accessReason = accessReason;
    }

    public String getCustomReason() {
        return customReason;
    }

    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }
}
