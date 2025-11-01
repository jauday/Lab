package com.lab.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lab.patientservice.model.LabPractice;

public interface LabPracticeRepository extends JpaRepository<LabPractice, Long> {

    Optional<LabPractice> findByCodeAndActiveTrue(String code);
}
