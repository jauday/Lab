package com.lab.patientservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponseDTO {

    private Long id;
    private LocalDate date;
    private Long patientId;
    private Long doctorId;
    private Long healthInsuranceId;
    private List<PracticeItem> practices;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AnalysisResponseDTO(Long id, LocalDate date, Long id1, Long id2, List<PracticeItem> practiceItems, LocalDateTime createdOn, LocalDateTime modifiedOn) {
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticeItem {
        private Long practiceId;
        private Double result;
    }
}