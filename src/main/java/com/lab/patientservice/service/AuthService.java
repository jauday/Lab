package com.lab.patientservice.service;

import com.lab.patientservice.dto.LoginRequest;
import com.lab.patientservice.dto.LoginResponse;
import com.lab.patientservice.dto.RegisterRequestDTO;
import com.lab.patientservice.model.LabUser;
import com.lab.patientservice.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LabUserService labUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String register(RegisterRequestDTO request) {
        // Encriptar la contraseña antes de guardar
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        labUserService.registerUser(request.getUsername(), encodedPassword);
        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request) {
        try {
            LabUser user = labUserService.findByUsername(request.getUsername());

            // Verificar la contraseña
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Invalid username or password");
            }

            // Generar token JWT
            String token = jwtTokenProvider.generateToken(user.getUsername());

            return new LoginResponse(token);
        } catch (RuntimeException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
