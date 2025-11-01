package com.lab.patientservice.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter @Setter
public class HealthInsurance extends BaseEntity{

    private String phone;
    private String email;
    private Double fee;

}

