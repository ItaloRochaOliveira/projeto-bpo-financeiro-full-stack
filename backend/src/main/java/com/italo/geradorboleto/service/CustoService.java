package com.italo.geradorboleto.service;

import com.italo.geradorboleto.dto.*;
import com.italo.geradorboleto.model.Custo;
import com.italo.geradorboleto.repository.CustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_UP;

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
        
        // Calcular totais por tipo de custo
        double somaCustoFixo = todosCustos.stream()
            .filter(custo -> "FIXO".equals(custo.getTipoCusto()))
            .mapToDouble(custo -> custo.getValor().doubleValue())
            .sum();

        double somaCustoVariavel = todosCustos.stream()
            .filter(custo -> "VARIÁVEL".equals(custo.getTipoCusto()))
            .mapToDouble(custo -> custo.getValor().doubleValue())
            .sum();

        double somaTotal = somaCustoFixo + somaCustoVariavel;

        double porcentagemFixo = somaTotal > 0 ? (somaCustoFixo / somaTotal) * 100 : 0;
        double porcentagemVariavel = somaTotal > 0 ? (somaCustoVariavel / somaTotal) * 100 : 0;

        // Criar lista de custos simples
        List<CustoSimplesResponse> custosSimples = todosCustos.stream()
            .map(this::convertToSimplesResponse)
            .collect(Collectors.toList());
        
        // Criar resumo
        ResumoCompletoResponse resumo = new ResumoCompletoResponse();
        
        ResumoCustosResponse custosFixos = new ResumoCustosResponse();
        custosFixos.setValue(BigDecimal.valueOf(somaCustoFixo));
        custosFixos.setPorcentagem(porcentagemFixo / 100);
        
        ResumoCustosResponse custosVariaveis = new ResumoCustosResponse();
        custosVariaveis.setValue(BigDecimal.valueOf(somaCustoVariavel));
        custosVariaveis.setPorcentagem(porcentagemVariavel / 100);
        
        ResumoCustosResponse total = new ResumoCustosResponse();
        total.setValue(BigDecimal.valueOf(somaTotal));
        total.setPorcentagem((porcentagemFixo + porcentagemVariavel) / 100);
        
        resumo.setCustosFixos(custosFixos);
        resumo.setCustosVariaveis(custosVariaveis);
        resumo.setTotal(total);
        
        // Montar resposta
        CustosArrayResponse response = new CustosArrayResponse();
        response.setData(custosSimples);
        response.setResumo(resumo);
        
        return response;
    }
    
    public CustoCompletoResponse getCustoById(String id, String userId) {
        Custo custo = custoRepository.findByIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        List<Custo> todosCustos = custoRepository.findByUserIdAndDeletedFalse(userId);
        
        double somaCustoFixo = todosCustos.stream()
            .filter(c -> "FIXO".equals(c.getTipoCusto()))
            .mapToDouble(c -> c.getValor().doubleValue())
            .sum();

        double somaCustoVariavel = todosCustos.stream()
            .filter(c -> "VARIÁVEL".equals(c.getTipoCusto()))
            .mapToDouble(c -> c.getValor().doubleValue())
            .sum();

        double somaTotal = somaCustoFixo + somaCustoVariavel;
        double porcentagemFixo = (somaCustoFixo / somaTotal) * 100;
        double porcentagemVariavel = (somaCustoVariavel / somaTotal) * 100;
        
        return convertToCompletoResponse(custo, todosCustos, somaTotal, somaCustoFixo, somaCustoVariavel, porcentagemFixo, porcentagemVariavel);
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
    
    private CustoSimplesResponse convertToSimplesResponse(Custo custo) {
        CustoSimplesResponse response = new CustoSimplesResponse();
        response.setId(custo.getId());
        response.setDescricao(custo.getDescricao());
        response.setValor(custo.getValor());
        response.setTipoCusto(custo.getTipoCusto());
        return response;
    }
    
    private CustoCompletoResponse convertToCompletoResponse(Custo custo, List<Custo> todosCustos, double somaTotal, double somaCustoFixo, double somaCustoVariavel, double porcentagemFixo, double porcentagemVariavel) {
        CustoCompletoResponse response = new CustoCompletoResponse();
        
        // Criar valueDB (sem porcentagem individual)
        CustoDetalheResponse valueDB = new CustoDetalheResponse();
        valueDB.setId(custo.getId());
        valueDB.setDescricao(custo.getDescricao());
        valueDB.setValor(custo.getValor());
        valueDB.setTipoCusto(custo.getTipoCusto());
        
        // Criar resumo (com porcentagens gerais)
        ResumoCompletoResponse resumo = new ResumoCompletoResponse();
        
        ResumoCustosResponse custosFixos = new ResumoCustosResponse();
        custosFixos.setValue(BigDecimal.valueOf(somaCustoFixo));
        custosFixos.setPorcentagem(porcentagemFixo / 100);
        
        ResumoCustosResponse custosVariaveis = new ResumoCustosResponse();
        custosVariaveis.setValue(BigDecimal.valueOf(somaCustoVariavel));
        custosVariaveis.setPorcentagem(porcentagemVariavel / 100);
        
        ResumoCustosResponse total = new ResumoCustosResponse();
        total.setValue(BigDecimal.valueOf(somaTotal));
        total.setPorcentagem((porcentagemFixo + porcentagemVariavel) / 100);
        
        resumo.setCustosFixos(custosFixos);
        resumo.setCustosVariaveis(custosVariaveis);
        resumo.setTotal(total);
        
        response.setValueDB(valueDB);
        response.setResumo(resumo);
        
        return response;
    }
}