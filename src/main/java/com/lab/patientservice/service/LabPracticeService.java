package com.lab.patientservice.service;


import org.springframework.stereotype.Service;
import com.lab.patientservice.model.LabPractice;
import com.lab.patientservice.repository.LabPracticeRepository;
import com.lab.patientservice.service.base.BaseServiceImpl;

@Service
public class LabPracticeService extends BaseServiceImpl<LabPractice, Long> {

    private final LabPracticeRepository labPracticeRepository;

    public LabPracticeService(LabPracticeRepository labPracticeRepository) {
        super(labPracticeRepository);
        this.labPracticeRepository = labPracticeRepository;
    }

}
