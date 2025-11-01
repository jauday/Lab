package com.lab.patientservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BillingReportDTO {
    private String LabName;
    private final String patientName;
    private final String healthInsuranceName;
    private final LocalDate practiceDate;
    private final List<String> practiceCodes;
    private final BigDecimal subTotal;


    // JasperReport: un solo campo con CSV de códigos
    public String getPracticeCodesCsv() {
        return String.join(", ", practiceCodes);
    }
}
