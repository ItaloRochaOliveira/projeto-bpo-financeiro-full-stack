package com.italo.geradorboleto.service;

import com.italo.geradorboleto.entity.User;
import com.italo.geradorboleto.repository.UserRepository;
import com.italo.geradorboleto.security.JwtTokenProvider;
import com.italo.geradorboleto.dto.AuthResponse;
import com.italo.geradorboleto.dto.LoginRequest;
import com.italo.geradorboleto.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

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
        
        String jwt = tokenProvider.generateToken(authentication);
        
        return new AuthResponse(jwt, user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setDeleted(false);

        User savedUser = userRepository.save(user);

        String jwt = tokenProvider.generateToken(savedUser.getEmail());
        
        return new AuthResponse(jwt, savedUser.getId(), savedUser.getEmail(), savedUser.getName(), savedUser.getRole());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("Usuário não autenticado");
    }
}
