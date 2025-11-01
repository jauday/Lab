package com.lab.patientservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lab.patientservice.model.AnalysisRequest;

@Repository
public interface AnalysisRequestRepository extends JpaRepository<AnalysisRequest, Long> {
    // Query principal - trae todo con relaciones
    @Query("""
        SELECT DISTINCT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE ar.active = true
        ORDER BY ar.createdOn DESC
        """)
    List<AnalysisRequest> findAllWithRelations();

    // Query por ID específico
    @Query("""
        SELECT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE ar.id = :id AND ar.active = true
        """)
    Optional<AnalysisRequest> findByIdWithRelations(@Param("id") Long id);

    // Query por paciente
    @Query("""
        SELECT DISTINCT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE p.id = :patientId AND ar.active = true
        ORDER BY ar.date DESC
        """)
    List<AnalysisRequest> findByPatientIdWithRelations(@Param("patientId") Long patientId);

    // Query por obra social
    @Query("""
        SELECT DISTINCT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE hi.id = :healthInsuranceId AND ar.active = true
        ORDER BY ar.date DESC
        """)
    List<AnalysisRequest> findByHealthInsuranceIdWithRelations(@Param("healthInsuranceId") Long healthInsuranceId);

    // Query por rango de fechas
    @Query("""
        SELECT DISTINCT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE ar.date BETWEEN :dateFrom AND :dateTo AND ar.active = true
        ORDER BY ar.date DESC
        """)
    List<AnalysisRequest> findByDateRangeWithRelations(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo
    );

    // Query por búsqueda de texto (ID, nombre paciente, DNI)
    @Query("""
        SELECT DISTINCT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE ar.active = true AND (
            CAST(ar.id AS string) LIKE %:searchTerm% OR
            LOWER(CONCAT(p.name, ' ', p.lastName)) LIKE %:searchTerm% OR
            LOWER(p.personalId) LIKE %:searchTerm% OR
            LOWER(p.name) LIKE %:searchTerm% OR
            LOWER(p.lastName) LIKE %:searchTerm%
        )
        ORDER BY ar.createdOn DESC
        """)
    List<AnalysisRequest> findBySearchTermWithRelations(@Param("searchTerm") String searchTerm);

    // Query compleja para múltiples filtros
    @Query("""
        SELECT DISTINCT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE ar.active = true
        AND (:search IS NULL OR :search = '' OR 
             CAST(ar.id AS string) LIKE %:search% OR
             LOWER(CONCAT(p.name, ' ', p.lastName)) LIKE %:search% OR
             LOWER(p.personalId) LIKE %:search%)
        AND (:patientId IS NULL OR p.id = :patientId)
        AND (:healthInsuranceId IS NULL OR hi.id = :healthInsuranceId)
        AND (:doctorId IS NULL OR d.id = :doctorId)
        AND (:dateFrom IS NULL OR ar.date >= :dateFrom)
        AND (:dateTo IS NULL OR ar.date <= :dateTo)
        ORDER BY ar.createdOn DESC
        """)
    List<AnalysisRequest> findWithComplexFilters(
            @Param("search") String search,
            @Param("patientId") Long patientId,
            @Param("healthInsuranceId") Long healthInsuranceId,
            @Param("doctorId") Long doctorId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo
    );

    // Queries adicionales útiles
    @Query("""
        SELECT DISTINCT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE d.id = :doctorId AND ar.active = true
        ORDER BY ar.date DESC
        """)
    List<AnalysisRequest> findByDoctorIdWithRelations(@Param("doctorId") Long doctorId);

    @Query("""
        SELECT COUNT(ar) FROM AnalysisRequest ar
        WHERE ar.patient.id = :patientId AND ar.active = true
        """)
    Long countByPatientId(@Param("patientId") Long patientId);

    @Query("""
        SELECT DISTINCT ar FROM AnalysisRequest ar
        LEFT JOIN FETCH ar.patient p
        LEFT JOIN FETCH ar.doctor d  
        LEFT JOIN FETCH ar.healthInsurance hi
        LEFT JOIN FETCH ar.practices ap
        LEFT JOIN FETCH ap.practice lp
        WHERE ar.date = :date AND ar.active = true
        ORDER BY ar.createdOn DESC
        """)
    List<AnalysisRequest> findByDateWithRelations(@Param("date") LocalDate date);
}


