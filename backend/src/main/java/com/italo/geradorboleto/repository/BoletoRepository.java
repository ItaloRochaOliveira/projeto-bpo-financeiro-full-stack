package com.italo.geradorboleto.repository;

import com.italo.geradorboleto.entity.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, String> {
    
    List<Boleto> findByUserIdAndDeletedFalse(String userId);
    
    @Query("SELECT b FROM Boleto b WHERE b.user.id = :userId AND b.deleted = false ORDER BY b.createdAt DESC")
    List<Boleto> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
    
    @Query("SELECT b FROM Boleto b WHERE b.id = :id AND b.deleted = false")
    Optional<Boleto> findByIdAndNotDeleted(@Param("id") String id);
    
    @Query("SELECT b FROM Boleto b WHERE b.user.id = :userId AND b.id = :id AND b.deleted = false")
    Optional<Boleto> findByIdAndUserIdAndNotDeleted(@Param("id") String id, @Param("userId") String userId);
}
