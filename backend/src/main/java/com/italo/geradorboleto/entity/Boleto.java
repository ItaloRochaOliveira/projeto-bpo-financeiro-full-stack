package com.italo.geradorboleto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "boleto_data")
@EntityListeners(AuditingEntityListener.class)
public class Boleto {
    
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "nome_empresa", length = 100)
    @Size(max = 100, message = "Nome da empresa deve ter no máximo 100 caracteres")
    private String nomeEmpresa;

    @Column(name = "cpf_cnpj", length = 18)
    @Size(max = 18, message = "CPF/CNPJ deve ter no máximo 18 caracteres")
    private String cpfCnpj;

    @Column(name = "endereco", length = 255)
    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    private String endereco;

    @Column(name = "descricao_referencia", length = 255)
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricaoReferencia;

    @Column(name = "valor", precision = 10, scale = 2)
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @Column(name = "vencimento")
    private LocalDate vencimento;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Boleto() {}

    public Boleto(String nomeEmpresa, String cpfCnpj, String endereco, 
                  String descricaoReferencia, BigDecimal valor, LocalDate vencimento, User user) {
        this.nomeEmpresa = nomeEmpresa;
        this.cpfCnpj = cpfCnpj;
        this.endereco = endereco;
        this.descricaoReferencia = descricaoReferencia;
        this.valor = valor;
        this.vencimento = vencimento;
        this.user = user;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
