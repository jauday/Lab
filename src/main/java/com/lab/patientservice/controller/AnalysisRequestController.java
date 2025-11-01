package com.lab.patientservice.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lab.patientservice.dto.AnalysisRequestListDTO;
import com.lab.patientservice.dto.CreateAnalysisRequestDTO;
import com.lab.patientservice.dto.CreateAnalysisResponseDTO;
import com.lab.patientservice.model.AnalysisRequest;
import com.lab.patientservice.service.AnalysisRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/analysis-requests")
@RequiredArgsConstructor
@Slf4j
public class AnalysisRequestController {

    private final AnalysisRequestService service;

    @GetMapping
    public ResponseEntity<List<AnalysisRequestListDTO>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long healthInsuranceId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        try {
            log.info("Fetching analysis requests with filters - search: {}, patientId: {}, healthInsuranceId: {}, dateFrom: {}, dateTo: {}",
                    search, patientId, healthInsuranceId, dateFrom, dateTo);

            List<AnalysisRequest> requests;

            // Usar diferentes métodos del service según los filtros aplicados
            if (hasFilters(search, patientId, healthInsuranceId, doctorId, dateFrom, dateTo)) {
                requests = service.getFilteredAnalysisRequests(search, patientId, healthInsuranceId, doctorId, dateFrom, dateTo);
            } else {
                requests = service.getAllWithRelations();
            }

            List<AnalysisRequestListDTO> dtos = requests.stream()
                    .map(this::convertToListDTO)
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} analysis requests", dtos.size());
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            log.error("Error fetching analysis requests", e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<CreateAnalysisResponseDTO> create(
            @Valid @RequestBody CreateAnalysisRequestDTO dto) {

        log.info("Creating new analysis request for patient: {}", dto.getPatientId());

        CreateAnalysisResponseDTO response = service.createAnalysisRequest(dto);

        log.info("Analysis request created successfully with ID: {}", response.id());

        return ResponseEntity
                .created(URI.create("/analysis-requests/" + response.id()))
                .body(response);
    }

    // Método auxiliar para verificar si hay filtros aplicados
    private boolean hasFilters(String search, Long patientId, Long healthInsuranceId,
            Long doctorId, LocalDate dateFrom, LocalDate dateTo) {
        return search != null || patientId != null || healthInsuranceId != null ||
                doctorId != null || dateFrom != null || dateTo != null;
    }

    // Método de conversión optimizado
    private AnalysisRequestListDTO convertToListDTO(AnalysisRequest request) {
        try {
            // Construir información del paciente
            AnalysisRequestListDTO.PatientInfo patientInfo = null;
            if (request.getPatient() != null) {
                patientInfo = AnalysisRequestListDTO.PatientInfo.builder()
                        .id(request.getPatient().getId())
                        .name(request.getPatient().getName())
                        .lastName(request.getPatient().getLastName())
                        .personalId(request.getPatient().getPersonalId())
                        .fullName(request.getPatient().getName() + " " + request.getPatient().getLastName())
                        .birthDate(request.getPatient().getBirthDate())
                        .email(request.getPatient().getEmail())
                        .phone(request.getPatient().getPhone())
                        .build();
            }

            // Construir información del doctor (opcional)
            AnalysisRequestListDTO.DoctorInfo doctorInfo = null;
            if (request.getDoctor() != null) {
                doctorInfo = AnalysisRequestListDTO.DoctorInfo.builder()
                        .id(request.getDoctor().getId())
                        .name(request.getDoctor().getName())
                        .fullName(request.getDoctor().getName())
                        .build();
            }

            // Construir información de la obra social
            AnalysisRequestListDTO.HealthInsuranceInfo insuranceInfo = null;
            if (request.getHealthInsurance() != null) {
                insuranceInfo = AnalysisRequestListDTO.HealthInsuranceInfo.builder()
                        .id(request.getHealthInsurance().getId())
                        .name(request.getHealthInsurance().getName())
                        .phone(request.getHealthInsurance().getPhone())
                        .email(request.getHealthInsurance().getEmail())
                        .fee(request.getHealthInsurance().getFee())
                        .build();
            }

            // Construir información de las prácticas
            List<AnalysisRequestListDTO.AnalysisPracticeInfo> practicesInfo =
                    (request.getPractices() != null) ?
                            request.getPractices().stream()
                                    .map(analysisPractice -> {
                                        String status = determineStatus(
                                                analysisPractice.getResult(),
                                                analysisPractice.getPractice().getReferenceValueLow(),
                                                analysisPractice.getPractice().getReferenceValueHigh()
                                        );

                                        return AnalysisRequestListDTO.AnalysisPracticeInfo.builder()
                                                .practiceId(analysisPractice.getPractice().getId())
                                                .practiceCode(analysisPractice.getPractice().getCode().toString())
                                                .practiceName(analysisPractice.getPractice().getName())
                                                .result(analysisPractice.getResult())
                                                .referenceValueLow(analysisPractice.getPractice().getReferenceValueLow())
                                                .referenceValueHigh(analysisPractice.getPractice().getReferenceValueHigh())
                                                .nbu(analysisPractice.getPractice().getNbu())
                                                .status(status)
                                                .build();
                                    })
                                    .collect(Collectors.toList()) :
                            List.of();

            // Calcular totales
            Integer totalPractices = practicesInfo.size();

            return AnalysisRequestListDTO.builder()
                    .id(request.getId())
                    .date(request.getDate())
                    .createdAt(request.getCreatedOn())
                    .updatedAt(request.getModifiedOn())
                    .patient(patientInfo)
                    .doctor(doctorInfo)
                    .healthInsurance(insuranceInfo)
                    .practices(practicesInfo)
                    .totalPractices(totalPractices)
                    // IDs para compatibilidad con frontend actual
                    .patientId(request.getPatient() != null ? request.getPatient().getId() : null)
                    .healthInsuranceId(request.getHealthInsurance() != null ? request.getHealthInsurance().getId() : null)
                    .doctorId(request.getDoctor() != null ? request.getDoctor().getId() : null)
                    .build();

        } catch (Exception e) {
            log.error("Error converting AnalysisRequest {} to DTO", request.getId(), e);
            throw new RuntimeException("Error converting analysis request to DTO", e);
        }
    }

    private String determineStatus(Double result, Double low, Double high) {
        if (result == null) return "PENDIENTE";
        if (low != null && result < low) return "BAJO";
        if (high != null && result > high) return "ALTO";
        return "NORMAL";
    }
}