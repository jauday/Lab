package com.lab.patientservice.service;

import com.lab.patientservice.dto.LoginRequest;
import com.lab.patientservice.dto.LoginResponse;
import com.lab.patientservice.dto.RegisterRequestDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LabUserService labUserService;

    public String register(RegisterRequestDTO request) {
        labUserService.registerUser(request.getUsername(), request.getPassword());
        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request) {

        return new LoginResponse("blabla");
    }
}