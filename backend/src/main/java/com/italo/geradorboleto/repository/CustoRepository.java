package com.italo.geradorboleto.repository;

import com.italo.geradorboleto.model.Custo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustoRepository extends JpaRepository<Custo, String> {
    
    @Query("SELECT c FROM Custo c WHERE c.deleted = false AND c.userId = :userId")
    List<Custo> findByUserIdAndDeletedFalse(@Param("userId") String userId);
    
    @Query("SELECT c FROM Custo c WHERE c.deleted = false AND c.id = :id AND c.userId = :userId")
    Optional<Custo> findByIdAndDeletedFalse(@Param("id") String id, @Param("userId") String userId);
    
    @Query("SELECT c FROM Custo c WHERE c.deleted = false AND c.userId = :userId AND c.tipoCusto = :tipoCusto")
    List<Custo> findByTipoCustoAndDeletedFalse(@Param("tipoCusto") String tipoCusto, @Param("userId") String userId);
    
    @Query("SELECT c FROM Custo c WHERE c.deleted = false AND c.userId = :userId AND c.descricao ILIKE %:descricao%")
    List<Custo> findByDescricaoContainingAndDeletedFalse(@Param("descricao") String descricao, @Param("userId") String userId);
    
    @Query("SELECT c FROM Custo c WHERE c.deleted = true AND c.userId = :userId")
    List<Custo> findByUserIdAndDeletedTrue(@Param("userId") String userId);
}
