package com.lab.patientservice.service.base;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_DOCTOR,
    ROLE_PATIENT;

    public String getRoleName() {
        return this.name();
    }
}
