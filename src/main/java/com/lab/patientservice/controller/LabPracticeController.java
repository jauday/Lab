package com.lab.patientservice.controller;

import com.lab.patientservice.controller.base.BaseController;
import com.lab.patientservice.dto.ApiResponse;
import com.lab.patientservice.model.LabPractice;
import com.lab.patientservice.service.LabPracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lab-practices")
@Tag(name = "Lab Practices", description = "Endpoints to manage lab practices")
public class LabPracticeController extends BaseController {

    private final LabPracticeService labPracticeService;

    public LabPracticeController(LabPracticeService service) {
        this.labPracticeService = service;
    }

    @GetMapping
    @Operation(summary = "Get all lab practices", description = "Returns a list of all active lab practices")
    public ResponseEntity<ApiResponse<List<LabPractice>>> getAll() {
        return ok(labPracticeService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a lab practice by ID", description = "Returns a lab practice if found by its ID")
    public ResponseEntity<ApiResponse<LabPractice>> getById(@PathVariable Long id) {
        return labPracticeService.getById(id)
                .map(this::ok)
                .orElseGet(() -> error("Lab Practice not found", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @Operation(summary = "Create a lab practice", description = "Creates a new lab practice and returns it")
    public ResponseEntity<ApiResponse<LabPractice>> create(@RequestBody LabPractice practice) {
        return ok(labPracticeService.save(practice));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a lab practice", description = "Marks a lab practice as inactive (soft delete)")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        try {
            labPracticeService.deleteById(id);
            return ok(null);
        } catch (Exception e) {
            return error("Failed to delete Lab Practice: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get a lab practice by code", description = "Finds a lab practice by its unique code if active")
    public ResponseEntity<ApiResponse<Object>> getByCode(@PathVariable String code) {
        return labPracticeService.findByCodeAndActiveTrue(code)
                .map(this::ok)
                .orElseGet(() -> error("Lab Practice not found", HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a lab practice", description = "Updates a lab practice by its ID")
    public ResponseEntity<ApiResponse<LabPractice>> update(
            @PathVariable Long id,
            @RequestBody LabPractice updatedPractice) {

        return labPracticeService.getById(id)
                .map(existing -> {
                    updatedPractice.setId(id); // aseguramos que se actualiza el correcto
                    return ok(labPracticeService.save(updatedPractice));
                })
                .orElseGet(() -> error("Lab Practice not found", HttpStatus.NOT_FOUND));
    }

}

