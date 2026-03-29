package com.italo.geradorboleto.repository;

import com.italo.geradorboleto.model.Preco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrecoRepository extends JpaRepository<Preco, String> {
    
    @Query("SELECT p FROM Preco p WHERE p.deleted = false AND p.userId = :userId")
    List<Preco> findByUserIdAndDeletedFalse(@Param("userId") String userId);
    
    @Query("SELECT p FROM Preco p WHERE p.deleted = false AND p.id = :id AND p.userId = :userId")
    Optional<Preco> findByIdAndDeletedFalse(@Param("id") String id, @Param("userId") String userId);
    
    @Query("SELECT p FROM Preco p WHERE p.deleted = false AND p.faturamentoId = :faturamentoId AND p.userId = :userId")
    List<Preco> findByFaturamentoIdAndDeletedFalse(@Param("faturamentoId") String faturamentoId, @Param("userId") String userId);
    
    @Query("SELECT p FROM Preco p WHERE p.deleted = false AND p.userId = :userId AND p.equipamento ILIKE %:equipamento%")
    List<Preco> findByEquipamentoContainingAndDeletedFalse(@Param("equipamento") String equipamento, @Param("userId") String userId);
    
    @Query("SELECT p FROM Preco p WHERE p.deleted = false AND p.userId = :userId AND p.equipamento = :equipamento")
    Optional<Preco> findByEquipamentoAndDeletedFalse(@Param("equipamento") String equipamento, @Param("userId") String userId);
    
    @Query("SELECT p FROM Preco p WHERE p.deleted = true AND p.userId = :userId")
    List<Preco> findByUserIdAndDeletedTrue(@Param("userId") String userId);
}
