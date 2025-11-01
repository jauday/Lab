package com.lab.patientservice.model;

import com.lab.patientservice.service.base.Role;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LabUser extends BaseEntity{
    String username;
    String password;
    Role role;

}
