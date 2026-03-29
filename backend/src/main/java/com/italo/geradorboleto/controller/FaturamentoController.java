package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.dto.FaturamentoRequest;
import com.italo.geradorboleto.dto.FaturamentoResponse;
import com.italo.geradorboleto.service.FaturamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faturamento")
@RequiredArgsConstructor
public class FaturamentoController {
    
    private final FaturamentoService faturamentoService;
    
    @PostMapping
    public ResponseEntity<FaturamentoResponse> createFaturamento(
            @Valid @RequestBody FaturamentoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            FaturamentoResponse response = faturamentoService.createFaturamento(request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
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
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            List<FaturamentoResponse> faturamentos = faturamentoService.getAllFaturamentos(userId);
            return ResponseEntity.ok(faturamentos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FaturamentoResponse> getFaturamentoById(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            FaturamentoResponse response = faturamentoService.getFaturamentoById(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FaturamentoResponse> updateFaturamento(
            @PathVariable String id,
            @Valid @RequestBody FaturamentoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            FaturamentoResponse response = faturamentoService.updateFaturamento(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteFaturamento(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            faturamentoService.softDeleteFaturamento(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteFaturamento(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            boolean isAdmin = isAdmin(userDetails);
            faturamentoService.hardDeleteFaturamento(id, userId, isAdmin);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/deleted")
    public ResponseEntity<List<FaturamentoResponse>> getDeletedFaturamentos(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            List<FaturamentoResponse> faturamentos = faturamentoService.getDeletedFaturamentos(userId);
            return ResponseEntity.ok(faturamentos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    private String getUserIdFromUserDetails(UserDetails userDetails) {
        // Implementar lógica para extrair userId do UserDetails
        // Por enquanto, retornar um valor mock
        return "92056f79-327d-4792-9543-ccf68f8597a9";
    }
    
    private boolean isAdmin(UserDetails userDetails) {
        // Implementar lógica para verificar se o usuário é admin
        // Por enquanto, retornar false
        return false;
    }
}
