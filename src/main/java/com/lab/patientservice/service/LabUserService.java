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

    public LabUser registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        LabUser user = new LabUser();
        user.setUsername(username);
        user.setRole(Role.ROLE_USER);

        return userRepository.save(user);
    }

    public LabUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}
