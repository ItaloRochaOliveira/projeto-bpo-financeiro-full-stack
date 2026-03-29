package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.dto.*;
import com.italo.geradorboleto.service.CustoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custos")
@RequiredArgsConstructor
public class CustoController {
    
    private final CustoService custoService;
    
    @PostMapping
    public ResponseEntity<CustoResponse> createCusto(
            @Valid @RequestBody CustoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            CustoResponse response = custoService.createCusto(request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<CustosArrayResponse> getAllCustosByUserId(
            @RequestParam String userId) {
        try {
            CustosArrayResponse custos = custoService.getAllCustos(userId);
            return ResponseEntity.ok(custos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping
    public ResponseEntity<CustosArrayResponse> getAllCustos(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            CustosArrayResponse custos = custoService.getAllCustos(userId);
            return ResponseEntity.ok(custos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CustoCompletoResponse> getCustoById(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            CustoCompletoResponse response = custoService.getCustoById(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/tipo/{tipoCusto}")
    public ResponseEntity<List<CustoResponse>> getCustosByTipo(
            @PathVariable String tipoCusto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            List<CustoResponse> custos = custoService.getCustosByTipo(tipoCusto, userId);
            return ResponseEntity.ok(custos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CustoResponse> updateCusto(
            @PathVariable String id,
            @Valid @RequestBody CustoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            CustoResponse response = custoService.updateCusto(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCusto(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            custoService.softDeleteCusto(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteCusto(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            boolean isAdmin = isAdmin(userDetails);
            custoService.hardDeleteCusto(id, userId, isAdmin);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/deleted")
    public ResponseEntity<List<CustoResponse>> getDeletedCustos(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = getUserIdFromUserDetails(userDetails);
            List<CustoResponse> custos = custoService.getDeletedCustos(userId);
            return ResponseEntity.ok(custos);
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
