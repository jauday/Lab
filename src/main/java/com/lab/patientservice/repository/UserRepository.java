package com.lab.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lab.patientservice.model.LabUser;

public interface UserRepository extends JpaRepository<LabUser, Long> {
    Optional<LabUser> findByUsername(String username);
}
