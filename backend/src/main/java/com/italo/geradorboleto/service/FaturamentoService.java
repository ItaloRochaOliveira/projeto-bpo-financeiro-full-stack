package com.italo.geradorboleto.service;

import com.italo.geradorboleto.dto.FaturamentoRequest;
import com.italo.geradorboleto.dto.FaturamentoResponse;
import com.italo.geradorboleto.model.Faturamento;
import com.italo.geradorboleto.repository.FaturamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FaturamentoService {
    
    private final FaturamentoRepository faturamentoRepository;
    
    public FaturamentoResponse createFaturamento(FaturamentoRequest request, String userId) {
        Faturamento faturamento = new Faturamento();
        faturamento.setEquipamento(request.getEquipamento());
        faturamento.setTotalEquipamento(request.getTotalEquipamento());
        faturamento.setMediaAlugados(request.getMediaAlugados());
        faturamento.setUserId(userId);
        
        Faturamento savedFaturamento = faturamentoRepository.save(faturamento);
        return convertToResponse(savedFaturamento);
    }
    
    public List<FaturamentoResponse> getAllFaturamentos(String userId) {
        return faturamentoRepository.findByUserIdAndDeletedFalse(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public FaturamentoResponse getFaturamentoById(String id, String userId) {
        Faturamento faturamento = faturamentoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Faturamento não encontrado"));
        return convertToResponse(faturamento);
    }
    
    public FaturamentoResponse updateFaturamento(String id, FaturamentoRequest request, String userId) {
        Faturamento faturamento = faturamentoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Faturamento não encontrado"));
        
        faturamento.setEquipamento(request.getEquipamento());
        faturamento.setTotalEquipamento(request.getTotalEquipamento());
        faturamento.setMediaAlugados(request.getMediaAlugados());
        
        Faturamento updatedFaturamento = faturamentoRepository.save(faturamento);
        return convertToResponse(updatedFaturamento);
    }
    
    public void softDeleteFaturamento(String id, String userId) {
        Faturamento faturamento = faturamentoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Faturamento não encontrado"));
        
        faturamento.setDeleted(true);
        faturamento.setDeletedAt(LocalDateTime.now());
        faturamentoRepository.save(faturamento);
    }
    
    public void hardDeleteFaturamento(String id, String userId, boolean isAdmin) {
        if (!isAdmin) {
            throw new RuntimeException("Apenas administradores podem excluir dados permanentemente");
        }
        
        Faturamento faturamento = faturamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Faturamento não encontrado"));
        
        if (!faturamento.getUserId().equals(userId)) {
            throw new RuntimeException("Você só pode excluir seus próprios dados");
        }
        
        faturamentoRepository.delete(faturamento);
    }
    
    public List<FaturamentoResponse> getDeletedFaturamentos(String userId) {
        return faturamentoRepository.findByUserIdAndDeletedTrue(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    private FaturamentoResponse convertToResponse(Faturamento faturamento) {
        FaturamentoResponse response = new FaturamentoResponse();
        response.setId(faturamento.getId());
        response.setEquipamento(faturamento.getEquipamento());
        response.setMediaAlugados(faturamento.getMediaAlugados());
        
        // Calcular porcentagem: média alugada / total equipamento
        BigDecimal porcentagem = faturamento.getTotalEquipamento().compareTo(BigDecimal.ZERO) > 0
            ? faturamento.getMediaAlugados().divide(faturamento.getTotalEquipamento(), 4, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;
        response.setPorcentagem(porcentagem);
        
        return response;
    }
}
