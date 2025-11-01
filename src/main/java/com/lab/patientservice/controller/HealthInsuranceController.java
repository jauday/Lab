package com.lab.patientservice.controller;

import com.lab.patientservice.controller.base.BaseController;
import com.lab.patientservice.dto.ApiResponse;
import com.lab.patientservice.model.HealthInsurance;
import com.lab.patientservice.service.HealthInsuranceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/health-insurances")
@Tag(name = "Health Insurance", description = "Endpoint to manage health insurance")
public class HealthInsuranceController extends BaseController {

    private final HealthInsuranceService healthInsuranceService;

    public HealthInsuranceController(HealthInsuranceService service) {
        this.healthInsuranceService = service;
    }

    @GetMapping
    @Operation(summary = "Get all health insurances", description = "Return a list of all active health insurances")
    public ResponseEntity<ApiResponse<List<HealthInsurance>>> getAll() {
        return ok(healthInsuranceService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a health insurance by ID", description = "Returns a health insurance if found by its ID")
    public ResponseEntity<ApiResponse<HealthInsurance>> getById(@PathVariable Long id) {
        return healthInsuranceService.getById(id)
                .map(this::ok)
                .orElseGet(() -> error("Health Insurance not found", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @Operation(summary = "Create a health insurance", description = "Creates a new health insurance and returns it")
    public ResponseEntity<ApiResponse<HealthInsurance>> create(@RequestBody HealthInsurance hi) {
        return ok(healthInsuranceService.save(hi));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a health insurance", description = "Marks a health insurance as inactive (soft delete)")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        try {
            healthInsuranceService.deleteById(id);
            return ok(null);
        } catch (Exception e) {
            return error("Failed to delete Health Insurance: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get a health insurance by code", description = "Finds a health insurance by its unique code if active")
    public ResponseEntity<ApiResponse<Object>> getByCode(@PathVariable String code) {
        return healthInsuranceService.findByCodeAndActiveTrue(code)
                .map(this::ok)
                .orElseGet(() -> error("Health Insurance not found", HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Health Insurance", description = "Updates a health insurance by its ID")
    public ResponseEntity<ApiResponse<HealthInsurance>> update(
            @PathVariable Long id,
            @RequestBody HealthInsurance updatedHealthInsurance) {

        return healthInsuranceService.getById(id)
                .map(existing -> {
                    updatedHealthInsurance.setId(id); // aseguramos que se actualiza el correcto
                    return ok(healthInsuranceService.save(updatedHealthInsurance));
                })
                .orElseGet(() -> error("Health Insurance not found", HttpStatus.NOT_FOUND));
    }

}
