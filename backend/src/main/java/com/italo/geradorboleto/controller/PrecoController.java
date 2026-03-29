package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.dto.PrecoRequest;
import com.italo.geradorboleto.dto.PrecoResponse;
import com.italo.geradorboleto.service.PrecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/precos")
@RequiredArgsConstructor
public class PrecoController {
    
    private final PrecoService precoService;
    
    @PostMapping
    public ResponseEntity<PrecoResponse> createPreco(
            @Valid @RequestBody PrecoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            PrecoResponse response = precoService.createPreco(request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
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
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            List<PrecoResponse> precos = precoService.getAllPrecos(userId);
            return ResponseEntity.ok(precos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PrecoResponse> getPrecoById(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            PrecoResponse response = precoService.getPrecoById(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PrecoResponse> updatePreco(
            @PathVariable String id,
            @Valid @RequestBody PrecoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            PrecoResponse response = precoService.updatePreco(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeletePreco(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            precoService.softDeletePreco(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeletePreco(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            boolean isAdmin = isAdmin(userDetails);
            precoService.hardDeletePreco(id, userId, isAdmin);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/deleted")
    public ResponseEntity<List<PrecoResponse>> getDeletedPrecos(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            List<PrecoResponse> precos = precoService.getDeletedPrecos(userId);
            return ResponseEntity.ok(precos);
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
