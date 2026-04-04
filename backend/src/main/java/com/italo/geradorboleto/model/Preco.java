package com.italo.geradorboleto.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "precos")
@Data
public class Preco {
    
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "equipamento", nullable = false)
    private String equipamento;
    
    @Column(name = "investimento", nullable = false, precision = 10, scale = 2)
    private BigDecimal investimento;
    
    @Column(name = "residual", nullable = false, precision = 10, scale = 2)
    private BigDecimal residual;
    
    @Column(name = "depreciacao_meses", nullable = false)
    private Integer depreciacaoMeses;
    
    @Column(name = "preco_atual_mensal", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoAtualMensal;
    
    @Column(name = "margem", nullable = false, precision = 10, scale = 2)
    private BigDecimal margem;
    
    @Column(name = "manutencao_atual", nullable = false, precision = 10, scale = 2)
    private BigDecimal manutencaoAtual;
    
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;
}
