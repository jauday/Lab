package com.lab.patientservice.repository;

import com.lab.patientservice.model.HealthInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthInsuranceRepository extends JpaRepository<HealthInsurance, Long> {
}
