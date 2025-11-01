package com.lab.patientservice.service;

import org.springframework.stereotype.Service;
import com.lab.patientservice.model.Patient;
import com.lab.patientservice.repository.PatientRepository;
import com.lab.patientservice.service.base.BaseServiceImpl;

@Service
public class PatientService extends BaseServiceImpl<Patient, Long> {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        super(patientRepository);
        this.patientRepository = patientRepository;
    }

    public boolean existsByPersonalId(String personalId) {
        return patientRepository.existsByPersonalId(personalId);
    }
}
