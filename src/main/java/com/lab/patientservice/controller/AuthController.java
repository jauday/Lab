package com.lab.patientservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lab.patientservice.dto.LoginRequest;
import com.lab.patientservice.dto.LoginResponse;
import com.lab.patientservice.dto.RegisterRequestDTO;
import com.lab.patientservice.repository.UserRepository;
import com.lab.patientservice.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        try {
            String result = authService.register(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setupFirstUser() {
        try {
            // Verificar si ya existe un usuario
            if (userRepository.count() > 0) {
                return ResponseEntity.badRequest().body("Setup already completed");
            }

            RegisterRequestDTO admin = new RegisterRequestDTO();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // Cambiar después del primer login

            authService.register(admin);
            return ResponseEntity.ok("Admin user created. Please change password immediately.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}