package com.italo.geradorboleto.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "custos")
@Data
public class Custo {
    
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "descricao", nullable = false)
    private String descricao;
    
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "tipo_custo", nullable = false, length = 50)
    private String tipoCusto;
    
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
