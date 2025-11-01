package com.lab.patientservice.controller;

import com.lab.patientservice.controller.base.BaseController;
import com.lab.patientservice.dto.ApiResponse;
import com.lab.patientservice.model.Patient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.lab.patientservice.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patients", description = "Endpoint to manage patients")
public class PatientController extends BaseController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get all patients", description = "Return a list of all active patients")
    public ResponseEntity<ApiResponse<List<Patient>>> getAll() {
        return ok(patientService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a patient by ID", description = "Returns a patient if found by its ID")
    public ResponseEntity<ApiResponse<Patient>> getById(@PathVariable Long id) {
        return patientService.getById(id)
                .map(this::ok)
                .orElseGet(() -> error("Patient not found", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @Operation(summary = "Create a patient", description = "Creates a new patient and returns it")
    public ResponseEntity<ApiResponse<Patient>> create(@RequestBody Patient patient) {
        return ok(patientService.save(patient));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a patient", description = "Marks a patient as inactive (soft delete)")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        try {
            patientService.deleteById(id);
            return ok(null);
        } catch (Exception e) {
            return error("Failed to delete patient: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get a patient by code", description = "Finds a patient by its unique code if active")
    public ResponseEntity<ApiResponse<Object>> getByCode(@PathVariable String code) {
        return patientService.findByCodeAndActiveTrue(code)
                .map(this::ok)
                .orElseGet(() -> error("Patient not found", HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Patient", description = "Updates a Patient by its ID")
    public ResponseEntity<ApiResponse<Patient>> update(
            @PathVariable Long id,
            @RequestBody Patient updatedPatient) {

        return patientService.getById(id)
                .map(existing -> {
                    updatedPatient.setId(id); // aseguramos que se actualiza el correcto
                    return ok(patientService.save(updatedPatient));
                })
                .orElseGet(() -> error("Patient not found", HttpStatus.NOT_FOUND));
    }
}
