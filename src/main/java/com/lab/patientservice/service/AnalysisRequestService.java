package com.lab.patientservice.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.lab.patientservice.dto.CreateAnalysisRequestDTO;
import com.lab.patientservice.dto.CreateAnalysisResponseDTO;
import com.lab.patientservice.exception.ResourceNotFoundException;
import com.lab.patientservice.model.AnalysisPractice;
import com.lab.patientservice.model.AnalysisRequest;
import com.lab.patientservice.model.HealthInsurance;
import com.lab.patientservice.model.LabPractice;
import com.lab.patientservice.model.Patient;
import com.lab.patientservice.repository.AnalysisRequestRepository;
import com.lab.patientservice.repository.HealthInsuranceRepository;
import com.lab.patientservice.repository.LabPracticeRepository;
import com.lab.patientservice.repository.PatientRepository;
import com.lab.patientservice.service.base.BaseServiceImpl;

import lombok.extern.log4j.Log4j2;


@Service
@Log4j2
public class AnalysisRequestService extends BaseServiceImpl<AnalysisRequest, Long> {

    private static final String DEFAULT_PRACTICE_CODE = "0001";

    private final PatientRepository patientRepository;
    private final HealthInsuranceRepository healthInsuranceRepository;
    private final LabPracticeRepository labPracticeRepository;
    private final AnalysisRequestRepository analysisRequestRepository;

    public AnalysisRequestService(PatientRepository patientRepository,
            HealthInsuranceRepository healthInsuranceRepository, LabPracticeRepository labPracticeRepository,
            AnalysisRequestRepository analysisRequestRepository) {
        super(analysisRequestRepository);
        this.patientRepository = patientRepository;
        this.healthInsuranceRepository = healthInsuranceRepository;
        this.labPracticeRepository = labPracticeRepository;
        this.analysisRequestRepository = analysisRequestRepository;
    }

    // Método optimizado que trae todas las relaciones
    @Transactional(readOnly = true)
    public List<AnalysisRequest> getAllWithRelations() {
        log.debug("Fetching all analysis requests with relations");
        return analysisRequestRepository.findAllWithRelations();
    }

    // Método principal para filtros dinámicos
    @Transactional(readOnly = true)
    public List<AnalysisRequest> getFilteredAnalysisRequests(
            String search,
            Long patientId,
            Long healthInsuranceId,
            Long doctorId,
            LocalDate dateFrom,
            LocalDate dateTo) {

        log.debug("Applying filters - search: {}, patientId: {}, healthInsuranceId: {}, doctorId: {}, dateFrom: {}, dateTo: {}",
                search, patientId, healthInsuranceId, doctorId, dateFrom, dateTo);

        // Si solo hay búsqueda por texto, usar query específica
        if (StringUtils.hasText(search) && patientId == null && healthInsuranceId == null &&
                doctorId == null && dateFrom == null && dateTo == null) {
            return analysisRequestRepository.findBySearchTermWithRelations(search.toLowerCase());
        }

        // Si solo hay filtro por paciente
        if (patientId != null && !StringUtils.hasText(search) && healthInsuranceId == null &&
                doctorId == null && dateFrom == null && dateTo == null) {
            return analysisRequestRepository.findByPatientIdWithRelations(patientId);
        }

        // Si solo hay filtro por obra social
        if (healthInsuranceId != null && !StringUtils.hasText(search) && patientId == null &&
                doctorId == null && dateFrom == null && dateTo == null) {
            return analysisRequestRepository.findByHealthInsuranceIdWithRelations(healthInsuranceId);
        }

        // Si solo hay filtro por rango de fechas
        if (dateFrom != null || dateTo != null) {
            LocalDate from = dateFrom != null ? dateFrom : LocalDate.of(1900, 1, 1);
            LocalDate to = dateTo != null ? dateTo : LocalDate.now().plusYears(1);

            if (!StringUtils.hasText(search) && patientId == null && healthInsuranceId == null && doctorId == null) {
                return analysisRequestRepository.findByDateRangeWithRelations(from, to);
            }
        }

        // Para filtros complejos, usar query dinámica
        return analysisRequestRepository.findWithComplexFilters(search, patientId, healthInsuranceId, doctorId, dateFrom, dateTo);
    }

    @Transactional(readOnly = true)
    public Optional<AnalysisRequest> getByIdWithRelations(Long id) {
        log.debug("Fetching analysis request {} with relations", id);
        return analysisRequestRepository.findByIdWithRelations(id);
    }

    // Métodos específicos para casos de uso comunes
    @Transactional(readOnly = true)
    public List<AnalysisRequest> getByPatientWithRelations(Long patientId) {
        return analysisRequestRepository.findByPatientIdWithRelations(patientId);
    }

    @Transactional(readOnly = true)
    public List<AnalysisRequest> getByHealthInsuranceWithRelations(Long healthInsuranceId) {
        return analysisRequestRepository.findByHealthInsuranceIdWithRelations(healthInsuranceId);
    }

    @Transactional(readOnly = true)
    public List<AnalysisRequest> getByDateRangeWithRelations(LocalDate from, LocalDate to) {
        return analysisRequestRepository.findByDateRangeWithRelations(from, to);
    }


    @Transactional
    public CreateAnalysisResponseDTO createAnalysisRequest(CreateAnalysisRequestDTO dto){
        // 1. Validar entidades relacionadas
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente no encontrado: " + dto.getPatientId()));
 /*       Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Médico no encontrado: " + dto.getDoctorId()));*/
        HealthInsurance insurance = healthInsuranceRepository.findById(dto.getHealthInsuranceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Obra social no encontrada: " + dto.getHealthInsuranceId()));

        // 2. Crear el AnalysisRequest (sin total ni prácticas aún)
        AnalysisRequest request = new AnalysisRequest();
        request.setDate(dto.getDate());
        request.setPatient(patient);
        //request.setDoctor(doctor);
        request.setHealthInsurance(insurance);

        // 3. Armar lista de AnalysisPractice y asegurar práctica por defecto
        List<AnalysisPractice> practices = new ArrayList<>();

        LabPractice defaultPractice = labPracticeRepository.findByCodeAndActiveTrue(DEFAULT_PRACTICE_CODE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Práctica por defecto no encontrada (code=" + DEFAULT_PRACTICE_CODE + ")"));
        practices.add(new AnalysisPractice(defaultPractice, request, 0.0));

        // 4. Agregar las prácticas desde el DTO
        for (CreateAnalysisRequestDTO.PracticeItem item : dto.getPractices()) {
            LabPractice labPractice = labPracticeRepository.findById(item.getPracticeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Práctica no encontrada: " + item.getPracticeId()));
            practices.add(new AnalysisPractice(
                    labPractice, request, item.getResult()
            ));
        }

        // 5. Vincular prácticas al request y persistir todo
        request.setPractices(practices);
        AnalysisRequest saved = analysisRequestRepository.save(request);

        // 6. Calcular total “on-the-fly” y devolver el DTO
        double total = calculateTotal(saved);
        return new CreateAnalysisResponseDTO(saved.getId(), total);
    }

    public Double calculateTotal(AnalysisRequest request) {

        double sumNbu = request.getPractices().stream()
                .mapToDouble(p -> p.getPractice().getNbu())
                .sum();
        double fee = request.getHealthInsurance().getFee();
        return sumNbu * fee;
    }
}