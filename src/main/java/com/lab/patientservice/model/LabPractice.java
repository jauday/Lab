package com.lab.patientservice.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter @Setter
public class LabPractice extends BaseEntity{

    private String code;
    private Double nbu;
    private Double referenceValueLow;
    private Double referenceValueHigh;

}
