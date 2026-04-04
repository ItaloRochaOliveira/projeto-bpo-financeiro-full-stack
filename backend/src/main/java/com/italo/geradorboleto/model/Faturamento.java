package com.italo.geradorboleto.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "faturamento")
@Data
public class Faturamento {
    
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "equipamento", nullable = false)
    private String equipamento;
    
    @Column(name = "total_equipamento", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalEquipamento;
    
    @Column(name = "media_alugados", nullable = false, precision = 10, scale = 2)
    private BigDecimal mediaAlugados;
    
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
    
    @Column(name = "preco_id", length = 36)
    private String precoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preco_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Preco preco;
}
