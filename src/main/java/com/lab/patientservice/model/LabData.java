package com.lab.patientservice.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter @Setter
public class LabData extends BaseEntity{

    private String name;
    private String biochemist;
    private String adress;
    private String phone;
    @Email(message = "Email must be valid")
    private String email;
}
