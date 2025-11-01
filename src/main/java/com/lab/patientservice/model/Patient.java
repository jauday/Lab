package com.lab.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter @Setter
public class Patient extends  BaseEntity{


    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Column(unique = true)
    private String personalId;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^[\\d\\s\\-()+]+$", message = "Phone number must be valid")
    private String phone;

    @ManyToOne
    @JoinColumn(name = "health_insurance_id")
    private HealthInsurance healthInsurance;

    private String healthInsurancePersonalId;

}
