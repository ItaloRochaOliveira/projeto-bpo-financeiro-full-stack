package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.dto.FaturamentoRequest;
import com.italo.geradorboleto.dto.FaturamentoResponse;
import com.italo.geradorboleto.service.FaturamentoService;
import com.italo.geradorboleto.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faturamento")
@RequiredArgsConstructor
@Slf4j
public class FaturamentoController {
    
    private final FaturamentoService faturamentoService;
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping
    public ResponseEntity<FaturamentoResponse> createFaturamento(
            @Valid @RequestBody FaturamentoRequest request,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Creating faturamento");
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Creating faturamento for userId: {}", userId);
            
            FaturamentoResponse response = faturamentoService.createFaturamento(request, userId);
            log.debug("Faturamento created successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error creating faturamento", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<FaturamentoResponse>> getAllFaturamentosByUserId(
            @RequestParam String userId) {
        try {
            List<FaturamentoResponse> faturamentos = faturamentoService.getAllFaturamentos(userId);
            return ResponseEntity.ok(faturamentos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<FaturamentoResponse>> getAllFaturamentos(
            HttpServletRequest httpRequest) {
        try {
            log.debug("Fetching all faturamentos");
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Fetching all faturamentos for userId: {}", userId);
            
            List<FaturamentoResponse> faturamentos = faturamentoService.getAllFaturamentos(userId);
            log.debug("Found {} faturamentos for userId: {}", faturamentos.size(), userId);
            return ResponseEntity.ok(faturamentos);
        } catch (RuntimeException e) {
            log.error("Error fetching faturamentos", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FaturamentoResponse> getFaturamentoById(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Fetching faturamento with ID: {}", id);
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Fetching faturamento with ID: {} for userId: {}", id, userId);
            
            FaturamentoResponse response = faturamentoService.getFaturamentoById(id, userId);
            log.debug("Faturamento found with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error fetching faturamento with ID: {}", id, e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FaturamentoResponse> updateFaturamento(
            @PathVariable String id,
            @Valid @RequestBody FaturamentoRequest request,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Updating faturamento with ID: {}", id);
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Updating faturamento with ID: {} for userId: {}", id, userId);
            
            FaturamentoResponse response = faturamentoService.updateFaturamento(id, request, userId);
            log.debug("Faturamento updated successfully with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating faturamento with ID: {}", id, e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteFaturamento(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Soft deleting faturamento with ID: {}", id);
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Soft deleting faturamento with ID: {} for userId: {}", id, userId);
            
            faturamentoService.softDeleteFaturamento(id, userId);
            log.debug("Faturamento soft deleted successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error soft deleting faturamento with ID: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteFaturamento(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            boolean isAdmin = false; // TODO: Implementar verificação de admin via token se necessário
            log.debug("Hard deleting faturamento with ID: {} for userId: {} (admin: {})", id, userId, isAdmin);
            
            faturamentoService.hardDeleteFaturamento(id, userId, isAdmin);
            log.debug("Faturamento hard deleted successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error hard deleting faturamento with ID: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/deleted")
    public ResponseEntity<List<FaturamentoResponse>> getDeletedFaturamentos(
            HttpServletRequest httpRequest) {
        try {
            log.debug("Fetching deleted faturamentos");
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Fetching deleted faturamentos for userId: {}", userId);
            
            List<FaturamentoResponse> faturamentos = faturamentoService.getDeletedFaturamentos(userId);
            log.debug("Found {} deleted faturamentos for userId: {}", faturamentos.size(), userId);
            return ResponseEntity.ok(faturamentos);
        } catch (RuntimeException e) {
            log.error("Error fetching deleted faturamentos", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Token não encontrado");
    }
}
