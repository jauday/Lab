package com.lab.patientservice.service;

import org.springframework.stereotype.Service;
import com.lab.patientservice.model.LabUser;
import com.lab.patientservice.repository.UserRepository;
import com.lab.patientservice.service.base.Role;

@Service
public class LabUserService {
    private final UserRepository userRepository;

    public LabUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registra un nuevo usuario. La contraseña debe venir ya encriptada desde AuthService
     */
    public LabUser registerUser(String username, String encodedPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        LabUser user = new LabUser();
        user.setUsername(username);
        user.setPassword(encodedPassword); // Ya viene encriptada
        user.setRole(Role.ROLE_USER);
        user.setActive(true);

        return userRepository.save(user);
    }

    public LabUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}