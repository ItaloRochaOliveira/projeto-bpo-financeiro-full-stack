package com.italo.geradorboleto.service;

import com.italo.geradorboleto.entity.Boleto;
import com.italo.geradorboleto.entity.User;
import com.italo.geradorboleto.repository.BoletoRepository;
import com.italo.geradorboleto.repository.UserRepository;
import com.italo.geradorboleto.dto.BoletoRequest;
import com.italo.geradorboleto.dto.BoletoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BoletoService {

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private UserRepository userRepository;

    public BoletoResponse createBoleto(BoletoRequest boletoRequest, String userEmail) {
        User user = userRepository.findByEmailAndNotDeleted(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Boleto boleto = new Boleto();
        boleto.setNomeEmpresa(boletoRequest.getNomeEmpresa());
        boleto.setCpfCnpj(boletoRequest.getCpfCnpj());
        boleto.setEndereco(boletoRequest.getEndereco());
        boleto.setDescricaoReferencia(boletoRequest.getDescricaoReferencia());
        boleto.setValor(new BigDecimal(boletoRequest.getValor()));
        
        if (boletoRequest.getVencimento() != null && !boletoRequest.getVencimento().isEmpty()) {
            try {
                // Tenta formato yyyy-MM-dd (padrão)
                boleto.setVencimento(LocalDate.parse(boletoRequest.getVencimento(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (Exception e1) {
                try {
                    // Tenta formato dd/MM/yyyy (brasileiro)
                    boleto.setVencimento(LocalDate.parse(boletoRequest.getVencimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } catch (Exception e2) {
                    throw new RuntimeException("Formato de data inválido. Use yyyy-MM-dd ou dd/MM/yyyy");
                }
            }
        }
        
        boleto.setUser(user);
        boleto.setDeleted(false);

        Boleto savedBoleto = boletoRepository.save(boleto);
        return convertToResponse(savedBoleto);
    }

    @Transactional(readOnly = true)
    public List<BoletoResponse> getBoletosByUser(String userEmail) {
        User user = userRepository.findByEmailAndNotDeleted(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Boleto> boletos = boletoRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return boletos.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoletoResponse getBoletoById(String id, String userEmail) {
        User user = userRepository.findByEmailAndNotDeleted(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Boleto boleto = boletoRepository.findByIdAndUserIdAndNotDeleted(id, user.getId())
            .orElseThrow(() -> new RuntimeException("Boleto não encontrado"));
        
        return convertToResponse(boleto);
    }

    private BoletoResponse convertToResponse(Boleto boleto) {
        BoletoResponse response = new BoletoResponse();
        response.setId(boleto.getId());
        response.setNomeEmpresa(boleto.getNomeEmpresa());
        response.setCpfCnpj(boleto.getCpfCnpj());
        response.setEndereco(boleto.getEndereco());
        response.setDescricaoReferencia(boleto.getDescricaoReferencia());
        response.setValor(boleto.getValor().doubleValue());
        
        if (boleto.getVencimento() != null) {
            response.setVencimento(boleto.getVencimento().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        response.setCreatedAt(boleto.getCreatedAt());
        response.setUpdatedAt(boleto.getUpdatedAt());
        response.setUserId(boleto.getUser().getId());
        
        return response;
    }
}
