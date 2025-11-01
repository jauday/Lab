package com.lab.patientservice.service;

import com.lab.patientservice.model.Doctor;
import com.lab.patientservice.repository.DoctorRepository;
import com.lab.patientservice.service.base.BaseServiceImpl;

public class DoctorService extends BaseServiceImpl<Doctor,Long> {

    public DoctorService(DoctorRepository doctorRepository){
        super(doctorRepository);
    }
}