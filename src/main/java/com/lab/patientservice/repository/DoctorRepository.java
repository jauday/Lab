package com.lab.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lab.patientservice.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}
