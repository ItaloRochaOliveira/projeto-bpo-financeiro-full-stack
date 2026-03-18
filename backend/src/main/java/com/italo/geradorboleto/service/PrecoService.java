package com.italo.geradorboleto.service;

import com.italo.geradorboleto.dto.PrecoRequest;
import com.italo.geradorboleto.dto.PrecoResponse;
import com.italo.geradorboleto.model.Preco;
import com.italo.geradorboleto.model.Faturamento;
import com.italo.geradorboleto.repository.PrecoRepository;
import com.italo.geradorboleto.repository.FaturamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PrecoService {
    
    private final PrecoRepository precoRepository;
    private final FaturamentoRepository faturamentoRepository;
    
    public PrecoResponse createPreco(PrecoRequest request, String userId) {
        // Verificar se o faturamento existe e pertence ao usuário
        if (request.getFaturamentoId() != null) {
            Faturamento faturamento = faturamentoRepository.findByIdAndDeletedFalse(request.getFaturamentoId(), userId)
                .orElseThrow(() -> new RuntimeException("Faturamento não encontrado"));
        }
        
        Preco preco = new Preco();
        preco.setEquipamento(request.getEquipamento());
        preco.setInvestimento(request.getInvestimento());
        preco.setResidual(request.getResidual());
        preco.setDepreciacaoMeses(request.getDepreciacaoMeses());
        preco.setPrecoAtualMensal(request.getPrecoAtualMensal());
        preco.setMargem(request.getMargem());
        preco.setManutencaoAtual(request.getManutencaoAtual());
        preco.setFaturamentoId(request.getFaturamentoId());
        preco.setUserId(userId);
        
        Preco savedPreco = precoRepository.save(preco);
        return convertToResponse(savedPreco);
    }
    
    public List<PrecoResponse> getAllPrecos(String userId) {
        return precoRepository.findByUserIdAndDeletedFalse(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public PrecoResponse getPrecoById(String id, String userId) {
        Preco preco = precoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Preço não encontrado"));
        return convertToResponse(preco);
    }
    
    public PrecoResponse updatePreco(String id, PrecoRequest request, String userId) {
        Preco preco = precoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Preço não encontrado"));
        
        // Verificar se o faturamento existe e pertence ao usuário
        if (request.getFaturamentoId() != null) {
            Faturamento faturamento = faturamentoRepository.findByIdAndDeletedFalse(request.getFaturamentoId(), userId)
                .orElseThrow(() -> new RuntimeException("Faturamento não encontrado"));
            preco.setFaturamentoId(request.getFaturamentoId());
        }
        
        preco.setEquipamento(request.getEquipamento());
        preco.setInvestimento(request.getInvestimento());
        preco.setResidual(request.getResidual());
        preco.setDepreciacaoMeses(request.getDepreciacaoMeses());
        preco.setPrecoAtualMensal(request.getPrecoAtualMensal());
        preco.setMargem(request.getMargem());
        preco.setManutencaoAtual(request.getManutencaoAtual());
        
        Preco updatedPreco = precoRepository.save(preco);
        return convertToResponse(updatedPreco);
    }
    
    public void softDeletePreco(String id, String userId) {
        Preco preco = precoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Preço não encontrado"));
        
        preco.setDeleted(true);
        preco.setDeletedAt(LocalDateTime.now());
        precoRepository.save(preco);
    }
    
    public void hardDeletePreco(String id, String userId, boolean isAdmin) {
        if (!isAdmin) {
            throw new RuntimeException("Apenas administradores podem excluir dados permanentemente");
        }
        
        Preco preco = precoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Preço não encontrado"));
        
        if (!preco.getUserId().equals(userId)) {
            throw new RuntimeException("Você só pode excluir seus próprios dados");
        }
        
        precoRepository.delete(preco);
    }
    
    public List<PrecoResponse> getDeletedPrecos(String userId) {
        return precoRepository.findByUserIdAndDeletedTrue(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    private PrecoResponse convertToResponse(Preco preco) {
        PrecoResponse response = new PrecoResponse();
        response.setId(preco.getId());
        response.setEquipamento(preco.getEquipamento());
        response.setInvestimento(preco.getInvestimento());
        response.setResidual(preco.getResidual());
        response.setDepreciacaoMeses(preco.getDepreciacaoMeses());
        response.setPrecoAtualMensal(preco.getPrecoAtualMensal());
        response.setMargem(preco.getMargem());
        response.setManutencaoAtual(preco.getManutencaoAtual());
        response.setFaturamentoId(preco.getFaturamentoId());
        response.setCreatedAt(preco.getCreatedAt());
        response.setUpdatedAt(preco.getUpdatedAt());
        response.setDeleted(preco.getDeleted());
        response.setDeletedAt(preco.getDeletedAt());
        response.setUserId(preco.getUserId());
        return response;
    }
}
