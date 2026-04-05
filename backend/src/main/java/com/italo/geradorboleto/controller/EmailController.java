package com.italo.geradorboleto.controller;

import com.italo.geradorboleto.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Endpoints para testes e configuração de email")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "Enviar Email de Teste", description = "Envia um email de teste para verificar a configuração")
    @PostMapping("/test")
    public ResponseEntity<String> sendTestEmail(@RequestParam String toEmail) {
        try {
            emailService.sendTestEmail(toEmail);
            log.info("Email de teste enviado para: {}", toEmail);
            return ResponseEntity.ok("Email de teste enviado com sucesso para: " + toEmail);
        } catch (Exception e) {
            log.error("Erro ao enviar email de teste para: {}", toEmail, e);
            return ResponseEntity.badRequest().body("Erro ao enviar email: " + e.getMessage());
        }
    }

    @Operation(summary = "Testar Notificação de Acesso", description = "Simula o envio de email de notificação para administradores")
    @PostMapping("/test-access-notification")
    public ResponseEntity<String> testAccessNotification(
            @RequestParam String userName,
            @RequestParam String userEmail,
            @RequestParam String loginInfo,
            @RequestParam(required = false, defaultValue = "TEMP123") String tempPassword) {
        try {
            emailService.sendAccessRequestNotification(userName, userEmail, loginInfo, tempPassword);
            log.info("Email de notificação de acesso enviado para administradores sobre: {}", userEmail);
            return ResponseEntity.ok("Email de notificação enviado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao enviar email de notificação de acesso", e);
            return ResponseEntity.badRequest().body("Erro ao enviar email: " + e.getMessage());
        }
    }

    @Operation(summary = "Testar Solicitação Completa", description = "Simula uma solicitação de acesso completa com senha temporária")
    @PostMapping("/test-full-request")
    public ResponseEntity<String> testFullRequest(
            @RequestParam String userName,
            @RequestParam String userEmail,
            @RequestParam String loginInfo) {
        try {
            // Gerar senha temporária como no AuthService
            String tempPassword = generateTempPassword();
            
            emailService.sendAccessRequestNotification(userName, userEmail, loginInfo, tempPassword);
            log.info("Email de notificação completo enviado para administradores sobre: {}", userEmail);
            return ResponseEntity.ok("Email de notificação completo enviado com sucesso. Senha temporária: " + tempPassword);
        } catch (Exception e) {
            log.error("Erro ao enviar email de notificação completa", e);
            return ResponseEntity.badRequest().body("Erro ao enviar email: " + e.getMessage());
        }
    }
    
    // Método para gerar senha temporária (igual ao AuthService)
    private String generateTempPassword() {
        String letters = org.apache.commons.lang3.RandomStringUtils.random(4, true, false);
        String numbers = org.apache.commons.lang3.RandomStringUtils.random(4, false, true);
        return letters + numbers;
    }
    @PostMapping("/test-access-confirmation")
    public ResponseEntity<String> testAccessConfirmation(
            @RequestParam String userName,
            @RequestParam String userEmail) {
        try {
            emailService.sendAccessRequestConfirmation(userName, userEmail);
            log.info("Email de confirmação enviado para: {}", userEmail);
            return ResponseEntity.ok("Email de confirmação enviado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao enviar email de confirmação", e);
            return ResponseEntity.badRequest().body("Erro ao enviar email: " + e.getMessage());
        }
    }

    @Operation(summary = "Testar Aprovação de Acesso", description = "Simula o envio de email de aprovação com senha temporária")
    @PostMapping("/test-access-approved")
    public ResponseEntity<String> testAccessApproved(
            @RequestParam String userName,
            @RequestParam String userEmail,
            @RequestParam String tempPassword) {
        try {
            emailService.sendAccessApprovedNotification(userName, userEmail, tempPassword);
            log.info("Email de aprovação enviado para: {}", userEmail);
            return ResponseEntity.ok("Email de aprovação enviado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao enviar email de aprovação", e);
            return ResponseEntity.badRequest().body("Erro ao enviar email: " + e.getMessage());
        }
    }
}
