package com.lab.patientservice.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnalysisRequestDTO {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "El patientId es obligatorio")
    private Long patientId;

    //@NotNull(message = "El doctorId es obligatorio")
    private Long doctorId;

    @NotNull(message = "El healthInsuranceId es obligatorio")
    private Long healthInsuranceId;

    @NotEmpty(message = "Debe indicar al menos una práctica")
    private List<PracticeItem> practices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticeItem {
        @NotNull(message = "El practiceId es obligatorio")
        private Long practiceId;

        @NotNull(message = "El resultado de la práctica es obligatorio")
        private Double result;
    }
}
