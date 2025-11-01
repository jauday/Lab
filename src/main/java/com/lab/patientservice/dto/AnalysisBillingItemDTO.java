package com.lab.patientservice.dto;

import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisBillingItemDTO {
    private Map<String, Double> practicesCodeToNbu;
    private LocalDate realizationDate;
    private String patientName;
    private String patientHealthInsurancePersonalId;
    private Double subTotal;
    private String practiceCodesCsv;

}
