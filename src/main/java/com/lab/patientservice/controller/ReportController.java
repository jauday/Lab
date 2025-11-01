package com.lab.patientservice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lab.patientservice.service.ReportService;

import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/analysis/{id}/pdf")
    public ResponseEntity<byte[]> downloadAnalysisPdf(@PathVariable Long id) throws JRException {
        byte[] pdfBytes = reportService.generateAnalysisPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename("analysis_" + id + ".pdf")
                        .build()
        );

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/billing-analysis/pdf")
    public ResponseEntity<byte[]> downloadAllAnalysesPdf(
            @RequestParam(required = false) List<Long> analysisIds) throws JRException {
        byte[] pdfBytes;

        pdfBytes = reportService.generateBillingAnalysesPdf(analysisIds);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(analysisIds != null && !analysisIds.isEmpty() ? "selected_analyses.pdf" : "all_analyses.pdf")
                        .build()
        );

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
