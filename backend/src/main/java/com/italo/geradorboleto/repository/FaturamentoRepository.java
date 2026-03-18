package com.italo.geradorboleto.repository;

import com.italo.geradorboleto.model.Faturamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaturamentoRepository extends JpaRepository<Faturamento, String> {
    
    @Query("SELECT f FROM Faturamento f WHERE f.deleted = false AND f.userId = :userId")
    List<Faturamento> findByUserIdAndDeletedFalse(@Param("userId") String userId);
    
    @Query("SELECT f FROM Faturamento f WHERE f.deleted = false AND f.id = :id AND f.userId = :userId")
    Optional<Faturamento> findByIdAndDeletedFalse(@Param("id") String id, @Param("userId") String userId);
    
    @Query("SELECT f FROM Faturamento f WHERE f.deleted = false AND f.userId = :userId AND f.equipamento ILIKE %:equipamento%")
    List<Faturamento> findByEquipamentoContainingAndDeletedFalse(@Param("equipamento") String equipamento, @Param("userId") String userId);
    
    @Query("SELECT f FROM Faturamento f WHERE f.deleted = true AND f.userId = :userId")
    List<Faturamento> findByUserIdAndDeletedTrue(@Param("userId") String userId);
}
