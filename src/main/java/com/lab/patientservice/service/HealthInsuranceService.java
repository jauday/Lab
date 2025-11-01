package com.lab.patientservice.service;

import org.springframework.stereotype.Service;
import com.lab.patientservice.model.HealthInsurance;
import com.lab.patientservice.repository.HealthInsuranceRepository;
import com.lab.patientservice.service.base.BaseServiceImpl;

@Service
public class HealthInsuranceService extends BaseServiceImpl<HealthInsurance, Long> {

    private final HealthInsuranceRepository healthInsuranceRepository;

    public HealthInsuranceService(HealthInsuranceRepository healthInsuranceRepository) {
        super(healthInsuranceRepository);
        this.healthInsuranceRepository = healthInsuranceRepository;
    }

}
