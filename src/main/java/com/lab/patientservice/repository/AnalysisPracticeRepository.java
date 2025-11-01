package com.lab.patientservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lab.patientservice.model.AnalysisPractice;

@Repository
public interface AnalysisPracticeRepository extends JpaRepository<AnalysisPractice, Long> {
    // no es estrictamente necesario si sólo guardas prácticas via AnalysisRequest,
    // pero lo incluyo por si luego quieres consultas directas

    List<AnalysisPractice> findAllByAnalysisRequestId(Long requestId);
}