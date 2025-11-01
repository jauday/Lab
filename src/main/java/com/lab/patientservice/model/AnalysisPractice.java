package com.lab.patientservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisPractice extends BaseEntity {

    @ManyToOne(optional = false)
    private LabPractice practice;

    @ManyToOne(optional = false)
    private AnalysisRequest analysisRequest;

    private Double result;

}


