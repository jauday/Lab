package com.lab.patientservice.repository;

import com.lab.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByPersonalId(String personalId);
}
