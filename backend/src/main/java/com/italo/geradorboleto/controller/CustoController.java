package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.dto.*;
import com.italo.geradorboleto.service.CustoService;
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
@RequestMapping("/custos")
@RequiredArgsConstructor
@Slf4j
public class CustoController {        
    private final CustoService custoService;
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping
    public ResponseEntity<CustoResponse> createCusto(
            @Valid @RequestBody CustoRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {
        try {
            log.debug("Creating custo for user: {}", userDetails.getUsername());

            // Validar se o userId do token corresponde ao userId do request
            String token = extractTokenFromRequest(httpRequest);
            String tokenUserId = tokenProvider.getUserIdFromToken(token);
            
            CustoResponse response = custoService.createCusto(request, tokenUserId);
            log.debug("Custo created successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error creating custo for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<CustosArrayResponse> getAllCustosByUserId(
            @RequestParam String userId) {
        try {
            log.debug("Fetching all custos for userId: {}", userId);
            CustosArrayResponse custos = custoService.getAllCustos(userId);
            log.debug("Found {} custos for userId: {}", custos.getData().size(), userId);
            return ResponseEntity.ok(custos);
        } catch (RuntimeException e) {
            log.error("Error fetching custos for userId: {}", userId, e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping
    public ResponseEntity<CustosArrayResponse> getAllCustos(
            HttpServletRequest request) {
        try {
            log.debug("=== INÍCIO getAllCustos ===");
            
            String token = extractTokenFromRequest(request);
            log.debug("Token extracted: {}", token != null ? "success" : "NULL");
            
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("UserId from token: {}", userId);
            log.debug("Fetching all custos for userId: {}", userId);
            
            CustosArrayResponse custos = custoService.getAllCustos(userId);
            log.debug("Found {} custos for userId: {}", custos.getData().size(), userId);
            log.debug("=== FIM getAllCustos (SUCCESS) ===");
            return ResponseEntity.ok(custos);
        } catch (RuntimeException e) {
            log.error("=== ERRO getAllCustos ===");
            log.error("Error message: {}", e.getMessage());
            log.error("Error fetching custos for userId", e);
            log.error("=== FIM ERRO getAllCustos ===");
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
    
    @GetMapping("/{id}")
    public ResponseEntity<CustoCompletoResponse> getCustoById(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            log.debug("Token extracted: {}", token != null ? "success" : "NULL");
            
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("UserId from token: {}", userId);
            log.debug("Fetching custo with ID: {} for user: {}", id, userDetails.getUsername());
            CustoCompletoResponse response = custoService.getCustoById(id, userDetails.getUsername());
            log.debug("Custo found with ID: {} for user: {}", id, userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error fetching custo with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/tipo/{tipoCusto}")
    public ResponseEntity<List<CustoResponse>> getCustosByTipo(
            @PathVariable String tipoCusto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.debug("Fetching custos by tipo: {} for user: {}", tipoCusto, userDetails.getUsername());
            List<CustoResponse> custos = custoService.getCustosByTipo(tipoCusto, userDetails.getUsername());
            log.debug("Found {} custos of tipo: {} for user: {}", custos.size(), tipoCusto, userDetails.getUsername());
            return ResponseEntity.ok(custos);
        } catch (RuntimeException e) {
            log.error("Error fetching custos by tipo: {} for user: {}", tipoCusto, userDetails.getUsername(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CustoResponse> updateCusto(
            @PathVariable String id,
            @Valid @RequestBody CustoRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            log.debug("Token extracted: {}", token != null ? "success" : "NULL");
            
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("UserId from token: {}", userId);
            log.debug("Updating custo with ID: {} for user: {}", id, userDetails.getUsername());
            CustoResponse response = custoService.updateCusto(id, request, userId);
            log.debug("Custo updated successfully with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating custo with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCusto(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            log.debug("Token extracted: {}", token != null ? "success" : "NULL");
            
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("UserId from token: {}", userId);
            
            log.debug("Soft deleting custo with ID: {} for user: {}", id, userDetails.getUsername());
            custoService.softDeleteCusto(id, userId);
            log.debug("Custo soft deleted successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error soft deleting custo with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteCusto(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            log.debug("Token extracted: {}", token != null ? "success" : "NULL");
            
            String userId = tokenProvider.getUserIdFromToken(token);
            log.debug("UserId from token: {}", userId);
            
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            log.debug("Hard deleting custo with ID: {} for user: {} (admin: {})", id, userDetails.getUsername(), isAdmin);
            custoService.hardDeleteCusto(id, userDetails.getUsername(), isAdmin);
            log.debug("Custo hard deleted successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error hard deleting custo with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/deleted")
    public ResponseEntity<List<CustoResponse>> getDeletedCustos(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.debug("Fetching deleted custos for user: {}", userDetails.getUsername());
            List<CustoResponse> custos = custoService.getDeletedCustos(userDetails.getUsername());
            log.debug("Found {} deleted custos for user: {}", custos.size(), userDetails.getUsername());
            return ResponseEntity.ok(custos);
        } catch (RuntimeException e) {
            log.error("Error fetching deleted custos for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
