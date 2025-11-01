package com.lab.patientservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisRequestListDTO {


    // Datos del análisis
    private Long id;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Datos del paciente (enriquecidos)
    private PatientInfo patient;

    // Datos del doctor (enriquecidos, opcional)
    private DoctorInfo doctor;

    // Datos de la obra social (enriquecidos)
    private HealthInsuranceInfo healthInsurance;

    // Prácticas realizadas (enriquecidas)
    private List<AnalysisPracticeInfo> practices;

    // Resumen calculado
    private Integer totalPractices;

    // IDs para compatibilidad con el frontend actual
    private Long patientId;
    private Long healthInsuranceId;
    private Long doctorId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PatientInfo {
        private Long id;
        private String name;
        private String lastName;
        private String personalId;
        private String fullName; // name + lastName
        private LocalDate birthDate;
        private String email;
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorInfo {
        private Long id;
        private String name;
        private String fullName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HealthInsuranceInfo {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private Double fee;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisPracticeInfo {
        private Long practiceId;
        private String practiceCode;
        private String practiceName;
        private Double result;
        private Double referenceValueLow;
        private Double referenceValueHigh;
        private Double nbu; // Valor de la práctica
        private String status; // "NORMAL", "ALTO", "BAJO" basado en valores de referencia
    }
}
