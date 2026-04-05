package com.italo.geradorboleto.service;

import com.italo.geradorboleto.entity.User;
import com.italo.geradorboleto.repository.UserRepository;
import com.italo.geradorboleto.security.JwtTokenProvider;
import com.italo.geradorboleto.dto.AuthResponse;
import com.italo.geradorboleto.dto.LoginRequest;
import com.italo.geradorboleto.dto.SignupRequest;
import com.italo.geradorboleto.dto.AccessRequest;
import com.italo.geradorboleto.dto.FirstPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EmailService emailService;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userRepository.findByEmailAndNotDeleted(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        String jwt = tokenProvider.generateToken(user.getEmail(), user.getId());
        
        return new AuthResponse(jwt, user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getFirstLogin());
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(signupRequest.getRole() != null ? signupRequest.getRole() : "USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setDeleted(false);

        User savedUser = userRepository.save(user);

        String jwt = tokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());
        
        return new AuthResponse(jwt, savedUser.getId(), savedUser.getEmail(), savedUser.getName(), savedUser.getRole());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("Usuário não autenticado");
    }

    public String requestAccess(AccessRequest accessRequest) {
        // Verificar se o email já existe
        Optional<User> existingUserOpt = userRepository.findByEmailAndNotDeleted(accessRequest.getEmail());
        
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            
            // Se usuário já existe e está com firstLogin = true, reenviar com nova senha
            if (existingUser.getFirstLogin()) {
                // Verificar se já fez solicitação hoje
                if (existingUser.getLastAccessRequest() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime lastRequest = existingUser.getLastAccessRequest();
                    
                    // Verificar se a última solicitação foi hoje (mesmo dia)
                    if (now.toLocalDate().isEqual(lastRequest.toLocalDate())) {
                        throw new RuntimeException("Você já fez uma solicitação de acesso hoje. Tente novamente amanhã.");
                    }
                }
                
                // Gerar nova senha temporária e atualizar usuário
                String tempPassword = generateTempPassword();
                existingUser.setPassword(passwordEncoder.encode(tempPassword));
                existingUser.setLastAccessRequest(LocalDateTime.now());
                existingUser.setUpdatedAt(LocalDateTime.now());
                userRepository.save(existingUser);
                
                // Reenviar emails com nova senha
                try {
                    emailService.sendAccessRequestNotification(accessRequest.getName(), accessRequest.getEmail(), accessRequest.getFullReason(), tempPassword);
                    emailService.sendAccessRequestConfirmation(accessRequest.getName(), accessRequest.getEmail());
                    log.info("Emails reenviados com sucesso para usuário existente: {}", accessRequest.getEmail());
                    return "Solicitação reenviada com sucesso! Verifique seu email.";
                } catch (Exception e) {
                    log.error("Erro ao reenviar emails para usuário existente: {}", accessRequest.getEmail(), e);
                    throw new RuntimeException("Erro ao reenviar emails. Tente novamente.");
                }
            } else {
                // Usuário já tem acesso completo
                throw new RuntimeException("Este email já possui acesso ao sistema. Tente fazer login.");
            }
        }

        // Criar usuário com senha temporária e firstLogin = true
        // Gerar senha temporária legível
        String tempPassword = generateTempPassword();
        
        User user = new User();
        user.setName(accessRequest.getName());
        user.setEmail(accessRequest.getEmail());
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setRole("USER");
        user.setFirstLogin(true);
        user.setLastAccessRequest(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setDeleted(false);

        userRepository.save(user);

        // Enviar emails
        try {
            emailService.sendAccessRequestNotification(accessRequest.getName(), accessRequest.getEmail(), accessRequest.getFullReason(), tempPassword);
            
            // Email de confirmação para o usuário
            emailService.sendAccessRequestConfirmation(accessRequest.getName(), accessRequest.getEmail());
            
            log.info("Emails enviados com sucesso para a solicitação de acesso de: {}", accessRequest.getEmail());
        } catch (Exception e) {
            log.error("Erro ao enviar emails para solicitação de acesso: {}", accessRequest.getEmail(), e);
            // Não falhar a solicitação se o email falhar
        }
        return tempPassword;
    }

    public void setFirstPassword(FirstPasswordRequest firstPasswordRequest, String token) {
        // Validar senhas
        if (!firstPasswordRequest.getNewPassword().equals(firstPasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("As senhas não conferem");
        }

        // Extrair email do token
        String userEmail = tokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        
        // Buscar usuário
        User user = userRepository.findByEmailAndNotDeleted(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificar se é primeiro login
        if (!user.getFirstLogin()) {
            throw new RuntimeException("Este usuário já definiu sua senha");
        }

        // Atualizar senha e marcar que não é mais primeiro login
        user.setPassword(passwordEncoder.encode(firstPasswordRequest.getNewPassword()));
        user.setFirstLogin(false);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Enviar email de confirmação de alteração de senha
        try {
            emailService.sendPasswordChangedNotification(user.getName(), userEmail);
            log.info("Email de alteração de senha enviado para: {}", userEmail);
        } catch (Exception e) {
            log.error("Erro ao enviar email de alteração de senha para: {}", userEmail, e);
            // Não falhar a operação se o email falhar
        }
    }

    // Método para gerar senha temporária legível
    private String generateTempPassword() {
        // Gerar senha com 8 caracteres: 4 letras + 4 números
        String letters = RandomStringUtils.random(4, true, false); // 4 letras maiúsculas
        String numbers = RandomStringUtils.random(4, false, true); // 4 números
        return letters + numbers;
    }
}
