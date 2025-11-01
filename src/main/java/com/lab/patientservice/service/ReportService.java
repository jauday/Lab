package com.lab.patientservice.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lab.patientservice.dto.AnalysisBillingItemDTO;
import com.lab.patientservice.model.AnalysisPractice;
import com.lab.patientservice.model.AnalysisRequest;
import com.lab.patientservice.model.LabPractice;
import com.lab.patientservice.repository.AnalysisPracticeRepository;
import com.lab.patientservice.repository.AnalysisRequestRepository;
import com.lab.patientservice.repository.LabPracticeRepository;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportService {

    @Autowired
    private AnalysisRequestRepository requestRepository;
    @Autowired
    private AnalysisPracticeRepository practiceRepository;
    @Autowired
    private LabPracticeRepository labPracticeRepository;

    public byte[] generateAnalysisPdf(Long requestId) throws JRException {
        AnalysisRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Analysis request not found"));

        List<AnalysisPractice> practices = practiceRepository.findAllByAnalysisRequestId(requestId);

        // Compile report
        InputStream jrStream = getClass().getResourceAsStream("/reports/analysis_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrStream);

        // Prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("patientName", request.getPatient().getName());
        params.put("healthInsurance", request.getHealthInsurance().getName());
        params.put("requestDate", request.getDate());
        //params.put("doctor", request.getDoctor().getName());

        // Prepare practices data
        List<Map<String, Object>> practicesData = practices.stream()
                 .map(practice -> Map.of(
                "code", (Object) practice.getPractice().getCode(),
                "name", (Object) practice.getPractice().getName(),
                "result", (Object) (practice.getResult() != null ? practice.getResult() : "")
        )).toList();

        // Generate report
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                params,
                new JRBeanCollectionDataSource(practicesData)
        );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    public byte[] generateBillingAnalysesPdf(List<Long> analysisIds) throws JRException {
        // If no IDs provided, get all analysis requests
        List<AnalysisRequest> requests = (analysisIds != null && !analysisIds.isEmpty())
                ? requestRepository.findAllById(analysisIds)
                : requestRepository.findAll();

        if (requests.isEmpty()) {
            throw new IllegalArgumentException("No analysis requests found");
        }

        // Validate all requests have the same health insurance
        validateSameHealthInsurance(requests);

        // Get default practice
        LabPractice defaultPractice = labPracticeRepository.findByCodeAndActiveTrue("1")
                .orElseThrow(() -> new IllegalStateException("Default practice code 0001 not found"));

        // Prepare billing items
        List<AnalysisBillingItemDTO> billingItems = requests.stream()
                .map(request -> createBillingItem(request, defaultPractice))
                .collect(Collectors.toList());

        // Calculate total
        Double total = billingItems.stream()
                .mapToDouble(AnalysisBillingItemDTO::getSubTotal)
                .sum();

        return generateBillingReport(requests.get(0).getHealthInsurance().getName(), billingItems, total);
    }

    private AnalysisBillingItemDTO createBillingItem(AnalysisRequest request, LabPractice defaultPractice) {
        // Get all practices including default one
        Map<String, Double> practicesNbu = new HashMap<>();

        // Add existing practices
        request.getPractices().forEach(practice ->
                practicesNbu.put(practice.getPractice().getCode(),
                        practice.getPractice().getNbu())
        );

        // Add default practice
        practicesNbu.put(defaultPractice.getCode(), defaultPractice.getNbu());

        String practiceCodesCsv = practicesNbu.keySet().stream()
                .collect(Collectors.joining(", "));

        Double subtotal = practicesNbu.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum() * request.getHealthInsurance().getFee();

        return AnalysisBillingItemDTO.builder()
                .practicesCodeToNbu(practicesNbu)
                .realizationDate(request.getDate())
                .patientName(request.getPatient().getName())
                .patientHealthInsurancePersonalId(Optional.ofNullable(
                        request.getPatient().getHealthInsurancePersonalId()).orElse(""))
                .subTotal(subtotal)
                .practiceCodesCsv(practiceCodesCsv)
                .build();
    }

    private byte[] generateBillingReport(String healthInsurance,
            List<AnalysisBillingItemDTO> items, Double total) throws JRException {
        // Compile report
        InputStream jrStream = getClass().getResourceAsStream("/reports/billing_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrStream);

        // Prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("healthInsurance", healthInsurance);
        params.put("total", total);

        // Generate report
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                params,
                new JRBeanCollectionDataSource(items)
        );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private void validateSameHealthInsurance(List<AnalysisRequest> requests) {
        long distinctInsurances = requests.stream()
                .map(req -> req.getHealthInsurance().getId())
                .distinct()
                .count();

        if (distinctInsurances > 1) {
            throw new IllegalArgumentException("Cannot generate billing report for different health insurances");
        }
    }

    private Double calculateSubTotal(List<AnalysisPractice> practices, Double fee) {
        return practices.stream()
                .map(p -> p.getPractice().getNbu() * fee)
                .reduce(0.0, Double::sum);
    }

    private byte[] generateReport(AnalysisRequest request, List<AnalysisRequest> analysisRequestList, Double subTotal)
            throws JRException {
        // Compile report
        InputStream jrStream = getClass().getResourceAsStream("/reports/billing_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrStream);

        // Prepare parameters
        Map<String, Object> params = new HashMap<>();
        params.put("healthInsurance", request.getHealthInsurance().getName());

        // Prepare practice codes
        String codesCsv = analysisRequestList.stream()
                .map(ar -> ar.getPractices().stream()
                        .map(p ->p.getPractice().getCode())
                .collect(Collectors.joining(", "))).toString();

        // Prepare bean
        Map<String,Object> bean = Map.of(
                "patientName", request.getPatient().getName(),
                "practiceCodesCsv", codesCsv,
                "subTotal", subTotal
        );

        // Generate report
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                params,
                new JRBeanCollectionDataSource(Collections.singletonList(bean))
        );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}