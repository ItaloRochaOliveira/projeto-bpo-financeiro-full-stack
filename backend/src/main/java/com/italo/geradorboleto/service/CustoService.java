package com.italo.geradorboleto.service;

import com.italo.geradorboleto.dto.CustoRequest;
import com.italo.geradorboleto.dto.CustoResponse;
import com.italo.geradorboleto.model.Custo;
import com.italo.geradorboleto.repository.CustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustoService {
    
    private final CustoRepository custoRepository;
    
    public CustoResponse createCusto(CustoRequest request, String userId) {
        Custo custo = new Custo();
        custo.setDescricao(request.getDescricao());
        custo.setValor(request.getValor());
        custo.setTipoCusto(request.getTipoCusto());
        custo.setUserId(userId);
        
        Custo savedCusto = custoRepository.save(custo);
        return convertToResponse(savedCusto);
    }
    
    public List<CustoResponse> getAllCustos(String userId) {
        return custoRepository.findByUserIdAndDeletedFalse(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public CustoResponse getCustoById(String id, String userId) {
        Custo custo = custoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        return convertToResponse(custo);
    }
    
    public CustoResponse updateCusto(String id, CustoRequest request, String userId) {
        Custo custo = custoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        custo.setDescricao(request.getDescricao());
        custo.setValor(request.getValor());
        custo.setTipoCusto(request.getTipoCusto());
        
        Custo updatedCusto = custoRepository.save(custo);
        return convertToResponse(updatedCusto);
    }
    
    public void softDeleteCusto(String id, String userId) {
        Custo custo = custoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        custo.setDeleted(true);
        custo.setDeletedAt(LocalDateTime.now());
        custoRepository.save(custo);
    }
    
    public void hardDeleteCusto(String id, String userId, boolean isAdmin) {
        if (!isAdmin) {
            throw new RuntimeException("Apenas administradores podem excluir dados permanentemente");
        }
        
        Custo custo = custoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        if (!custo.getUserId().equals(userId)) {
            throw new RuntimeException("Você só pode excluir seus próprios dados");
        }
        
        custoRepository.delete(custo);
    }
    
    public List<CustoResponse> getDeletedCustos(String userId) {
        return custoRepository.findByUserIdAndDeletedTrue(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public List<CustoResponse> getCustosByTipo(String tipoCusto, String userId) {
        return custoRepository.findByTipoCustoAndDeletedFalse(tipoCusto, userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    private CustoResponse convertToResponse(Custo custo) {
        CustoResponse response = new CustoResponse();
        response.setId(custo.getId());
        response.setDescricao(custo.getDescricao());
        response.setValor(custo.getValor());
        response.setTipoCusto(custo.getTipoCusto());
        response.setCreatedAt(custo.getCreatedAt());
        response.setUpdatedAt(custo.getUpdatedAt());
        response.setDeleted(custo.getDeleted());
        response.setDeletedAt(custo.getDeletedAt());
        response.setUserId(custo.getUserId());
        return response;
    }
}
