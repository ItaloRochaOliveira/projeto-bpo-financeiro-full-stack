package com.italo.geradorboleto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class BoletoRequest {

    @NotBlank(message = "Nome da empresa é obrigatório")
    @Size(max = 100, message = "Nome da empresa deve ter no máximo 100 caracteres")
    private String nomeEmpresa;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Size(max = 18, message = "CPF/CNPJ deve ter no máximo 18 caracteres")
    private String cpfCnpj;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    private String endereco;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricaoReferencia;

    @NotBlank(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private String valor;

    private String vencimento;

    public BoletoRequest() {}

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDescricaoReferencia() {
        return descricaoReferencia;
    }

    public void setDescricaoReferencia(String descricaoReferencia) {
        this.descricaoReferencia = descricaoReferencia;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }
}
