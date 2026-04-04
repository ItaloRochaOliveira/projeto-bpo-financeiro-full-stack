package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.dto.PrecoRequest;
import com.italo.geradorboleto.dto.PrecoResponse;
import com.italo.geradorboleto.service.PrecoService;
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
@RequestMapping("/precos")
@RequiredArgsConstructor
@Slf4j
public class PrecoController {
    
    private final PrecoService precoService;
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping
    public ResponseEntity<PrecoResponse> createPreco(
            @Valid @RequestBody PrecoRequest request,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Creating preco");
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Creating preco for userId: {}", userId);
            
            PrecoResponse response = precoService.createPreco(request, userId);
            log.debug("Preco created successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error creating preco", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<PrecoResponse>> getAllPrecosByUserId(
            @RequestParam String userId) {
        try {
            List<PrecoResponse> precos = precoService.getAllPrecos(userId);
            return ResponseEntity.ok(precos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<PrecoResponse>> getAllPrecos(
            HttpServletRequest httpRequest) {
        try {
            log.debug("Fetching all precos");
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Fetching all precos for userId: {}", userId);
            
            List<PrecoResponse> precos = precoService.getAllPrecos(userId);
            log.debug("Found {} precos for userId: {}", precos.size(), userId);
            return ResponseEntity.ok(precos);
        } catch (RuntimeException e) {
            log.error("Error fetching precos", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PrecoResponse> getPrecoById(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Fetching preco with ID: {}", id);
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Fetching preco with ID: {} for userId: {}", id, userId);
            
            PrecoResponse response = precoService.getPrecoById(id, userId);
            log.debug("Preco found with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error fetching preco with ID: {}", id, e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PrecoResponse> updatePreco(
            @PathVariable String id,
            @Valid @RequestBody PrecoRequest request,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Updating preco with ID: {}", id);
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Updating preco with ID: {} for userId: {}", id, userId);
            
            PrecoResponse response = precoService.updatePreco(id, request, userId);
            log.debug("Preco updated successfully with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating preco with ID: {}", id, e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeletePreco(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Soft deleting preco with ID: {}", id);
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Soft deleting preco with ID: {} for userId: {}", id, userId);
            
            precoService.softDeletePreco(id, userId);
            log.debug("Preco soft deleted successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error soft deleting preco with ID: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeletePreco(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            boolean isAdmin = false; // TODO: Implementar verificação de admin via token se necessário
            log.debug("Hard deleting preco with ID: {} for userId: {} (admin: {})", id, userId, isAdmin);
            
            precoService.hardDeletePreco(id, userId, isAdmin);
            log.debug("Preco hard deleted successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error hard deleting preco with ID: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/deleted")
    public ResponseEntity<List<PrecoResponse>> getDeletedPrecos(
            HttpServletRequest httpRequest) {
        try {
            log.debug("Fetching deleted precos");
            String token = extractTokenFromRequest(httpRequest);
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("Fetching deleted precos for userId: {}", userId);
            
            List<PrecoResponse> precos = precoService.getDeletedPrecos(userId);
            log.debug("Found {} deleted precos for userId: {}", precos.size(), userId);
            return ResponseEntity.ok(precos);
        } catch (RuntimeException e) {
            log.error("Error fetching deleted precos", e);
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
