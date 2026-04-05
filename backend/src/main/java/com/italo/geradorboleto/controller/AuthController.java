package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.dto.AuthResponse;
import com.italo.geradorboleto.dto.LoginRequest;
import com.italo.geradorboleto.dto.SignupRequest;
import com.italo.geradorboleto.dto.AccessRequest;
import com.italo.geradorboleto.dto.FirstPasswordRequest;
import com.italo.geradorboleto.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            AuthResponse response = authService.signup(signupRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/request-access")
    public ResponseEntity<?> requestAccess(@Valid @RequestBody AccessRequest accessRequest) {
        try {
            authService.requestAccess(accessRequest);
            return ResponseEntity.ok("Solicitação de acesso enviada com sucesso! Aguarde aprovação.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/first-password")
    public ResponseEntity<?> setFirstPassword(@Valid @RequestBody FirstPasswordRequest firstPasswordRequest, 
                                            @RequestHeader("Authorization") String token) {
        try {
            authService.setFirstPassword(firstPasswordRequest, token);
            return ResponseEntity.ok("Senha definida com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
