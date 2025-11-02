package com.lab.patientservice.service;

import org.springframework.stereotype.Service;
import com.lab.patientservice.model.Doctor;
import com.lab.patientservice.repository.DoctorRepository;
import com.lab.patientservice.service.base.BaseServiceImpl;
@Service
public class DoctorService extends BaseServiceImpl<Doctor,Long> {

    public DoctorService(DoctorRepository doctorRepository){
        super(doctorRepository);
    }
}