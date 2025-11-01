package com.lab.patientservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalysisRequest extends BaseEntity {

    private LocalDate date;

    @ManyToOne(optional = false)
    private Patient patient;

    @ManyToOne()
    private Doctor doctor;

    @ManyToOne(optional = false)
    private HealthInsurance healthInsurance;

    @Singular("practice")
    @OneToMany(
            mappedBy = "analysisRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AnalysisPractice> practices;

}





