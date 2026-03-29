package com.italo.geradorboleto.service;

import com.italo.geradorboleto.dto.*;
import com.italo.geradorboleto.model.Custo;
import com.italo.geradorboleto.repository.CustoRepository;
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
    
    public CustosArrayResponse getAllCustos(String userId) {
        List<Custo> todosCustos = custoRepository.findByUserIdAndDeletedFalse(userId);
        
        List<CustoCompletoResponse> custosCompletos = todosCustos.stream()
            .map(custo -> convertToCompletoResponse(custo, todosCustos))
            .collect(Collectors.toList());
        
        CustosArrayResponse response = new CustosArrayResponse();
        response.setData(custosCompletos);
        return response;
    }
    
    public CustoCompletoResponse getCustoById(String id, String userId) {
        Custo custo = custoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        List<Custo> todosCustos = custoRepository.findByUserIdAndDeletedFalse(userId);
        return convertToCompletoResponse(custo, todosCustos);
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
    
    private CustoCompletoResponse convertToCompletoResponse(Custo custo, List<Custo> todosCustos) {
        CustoCompletoResponse response = new CustoCompletoResponse();
        
        // Calcular valores
        BigDecimal valueCustoFixo = todosCustos.stream()
            .filter(c -> "FIXO".equalsIgnoreCase(c.getTipoCusto()))
            .map(Custo::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal valueCustoVariavel = todosCustos.stream()
            .filter(c -> "VARIAVEL".equalsIgnoreCase(c.getTipoCusto()))
            .map(Custo::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal soma = valueCustoFixo.add(valueCustoVariavel);
        
        // Calcular porcentagens
        BigDecimal porcentagemValorFixo = soma.compareTo(BigDecimal.ZERO) > 0 
            ? valueCustoFixo.divide(soma, 4, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;
        
        BigDecimal porcentagemValorVariavel = soma.compareTo(BigDecimal.ZERO) > 0 
            ? valueCustoVariavel.divide(soma, 4, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;
        
        // Calcular porcentagem individual do custo
        BigDecimal porcentagemIndividual = soma.compareTo(BigDecimal.ZERO) > 0 
            ? custo.getValor().divide(soma, 4, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;
        
        // Criar valueDB
        CustoDetalheResponse valueDB = new CustoDetalheResponse();
        valueDB.setId(custo.getId());
        valueDB.setDescricao(custo.getDescricao());
        valueDB.setValor(custo.getValor());
        valueDB.setTipoCusto(custo.getTipoCusto());
        valueDB.setPorcentagem(porcentagemIndividual);
        
        // Criar resumo
        ResumoCompletoResponse resumo = new ResumoCompletoResponse();
        
        ResumoCustosResponse custosFixos = new ResumoCustosResponse();
        custosFixos.setValue(valueCustoFixo);
        custosFixos.setPorcentagem(porcentagemValorFixo);
        
        ResumoCustosResponse custosVariaveis = new ResumoCustosResponse();
        custosVariaveis.setValue(valueCustoVariavel);
        custosVariaveis.setPorcentagem(porcentagemValorVariavel);
        
        ResumoCustosResponse total = new ResumoCustosResponse();
        total.setValue(soma);
        total.setPorcentagem(porcentagemValorFixo.add(porcentagemValorVariavel));
        
        resumo.setCustosFixos(custosFixos);
        resumo.setCustosVariaveis(custosVariaveis);
        resumo.setTotal(total);
        
        response.setValueDB(valueDB);
        response.setResumo(resumo);
        
        return response;
    }
}
