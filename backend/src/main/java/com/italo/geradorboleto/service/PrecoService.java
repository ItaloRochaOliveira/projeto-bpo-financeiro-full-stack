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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PrecoService {
    
    private final PrecoRepository precoRepository;
    private final FaturamentoRepository faturamentoRepository;
    
    public PrecoResponse createPreco(PrecoRequest request, String userId) {
        Preco preco = new Preco();
        preco.setEquipamento(request.getEquipamento());
        preco.setInvestimento(request.getInvestimento());
        preco.setResidual(request.getResidual());
        preco.setDepreciacaoMeses(request.getDepreciacaoMeses());
        preco.setPrecoAtualMensal(request.getPrecoAtualMensal());
        preco.setMargem(request.getMargem());
        preco.setManutencaoAtual(request.getManutencaoAtual());
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
        response.setManutencaoAnual(preco.getManutencaoAtual());
        
        // Buscar dados do faturamento para este equipamento
        Optional<Faturamento> faturamentoOpt = faturamentoRepository.findByUserIdAndDeletedFalse(preco.getUserId())
            .stream()
            .filter(f -> preco.getEquipamento().equals(f.getEquipamento()))
            .findFirst();
        
        BigDecimal qtde = BigDecimal.ZERO;
        BigDecimal taxaOcupacao = BigDecimal.ZERO;
        BigDecimal mediaAlugados = BigDecimal.ZERO;
        
        if (faturamentoOpt.isPresent()) {
            Faturamento faturamento = faturamentoOpt.get();
            qtde = faturamento.getTotalEquipamento();
            taxaOcupacao = faturamento.getTotalEquipamento().compareTo(BigDecimal.ZERO) > 0
                ? faturamento.getMediaAlugados().divide(faturamento.getTotalEquipamento(), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            mediaAlugados = faturamento.getMediaAlugados();
        }
        
        response.setQtde(qtde);
        response.setTaxaOcupacao(taxaOcupacao);
        response.setMediaAlugados(mediaAlugados);
        
        // Buscar todos os preços para cálculos de rateio
        List<Preco> todosPrecos = precoRepository.findByUserIdAndDeletedFalse(preco.getUserId());
        
        // Cálculo do rateio: ((investimento - residual) * média alugados) / SOMARPRODUTO(investimento - residual; média alugados)
        BigDecimal investimentoMenosResidual = preco.getInvestimento().subtract(preco.getResidual());
        BigDecimal numeradorRateio = investimentoMenosResidual.multiply(mediaAlugados);
        
        BigDecimal denominadorRateio = todosPrecos.stream()
            .map(p -> {
                Optional<Faturamento> fatOpt = faturamentoRepository.findByUserIdAndDeletedFalse(preco.getUserId())
                    .stream()
                    .filter(f -> p.getEquipamento().equals(f.getEquipamento()))
                    .findFirst();
                BigDecimal media = fatOpt.map(Faturamento::getMediaAlugados).orElse(BigDecimal.ZERO);
                return p.getInvestimento().subtract(p.getResidual()).multiply(media);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal rateio = denominadorRateio.compareTo(BigDecimal.ZERO) > 0
            ? numeradorRateio.divide(denominadorRateio, 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        response.setRateio(rateio);
        
        // Custo rateado: Custos Fixos e Variáveis'!$D$7 * rateio (valor fixo simulado)
        BigDecimal custosFixosVariaveisD7 = new BigDecimal("1000.00"); // Valor simulado da célula D7
        BigDecimal custoRateado = custosFixosVariaveisD7.multiply(rateio);
        response.setCustoRateado(custoRateado);
        
        // Manutenção mensal: manutenção anual / 12
        BigDecimal manutencaoMensal = preco.getManutencaoAtual().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        response.setManutencaoMensal(manutencaoMensal);
        
        // Custo: custo rateado / média alugados
        BigDecimal custo = mediaAlugados.compareTo(BigDecimal.ZERO) > 0
            ? custoRateado.divide(mediaAlugados, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        response.setCusto(custo);
        
        // Depreciação: (investimento - residual) / depreciação meses
        BigDecimal depreciacao = preco.getDepreciacaoMeses() > 0
            ? investimentoMenosResidual.divide(new BigDecimal(preco.getDepreciacaoMeses()), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        response.setDepreciacao(depreciacao);
        
        // Lucro: (investimento - residual) * margem
        BigDecimal lucro = investimentoMenosResidual.multiply(preco.getMargem());
        response.setLucro(lucro);
        
        // Ponto de equilíbrio: soma(manutenção mensal : lucro)
        BigDecimal pontoEquilibrio = manutencaoMensal.add(lucro);
        response.setPontoEquilibrio(pontoEquilibrio);
        
        // Preço adequado: soma(lucro : ponto de equilíbrio)
        BigDecimal precoAdequado = lucro.add(pontoEquilibrio);
        response.setPrecoAdequado(precoAdequado);
        
        // Preço atual - preço adequado
        BigDecimal precoAtualMenosPrecoAdequado = preco.getPrecoAtualMensal().subtract(precoAdequado);
        response.setPrecoAtualMenosPrecoAdequado(precoAtualMenosPrecoAdequado);
        
        // Faturamento estimado: preço adequado * média alugados
        BigDecimal faturamentoEstimado = precoAdequado.multiply(mediaAlugados);
        response.setFaturamentoEstimado(faturamentoEstimado);
        
        // Resultado: faturamento estimado - custo rateado
        BigDecimal resultado = faturamentoEstimado.subtract(custoRateado);
        response.setResultado(resultado);
        
        // Lucro total: lucro * média alugados
        BigDecimal lucroTotal = lucro.multiply(mediaAlugados);
        response.setLucroTotal(lucroTotal);
        
        // Payback meses: (investimento - residual) * qtde / resultado
        BigDecimal paybackMeses = resultado.compareTo(BigDecimal.ZERO) > 0
            ? investimentoMenosResidual.multiply(qtde).divide(resultado, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        response.setPaybackMeses(paybackMeses);
        
        return response;
    }
}
