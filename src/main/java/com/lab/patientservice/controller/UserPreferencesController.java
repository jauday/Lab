package com.lab.patientservice.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserPreferencesController {

    @PostMapping("/user/preferences/columns")
    public ResponseEntity<?> saveColumnPreferences(
    ) {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/preferences/columns/{tableName}")
    public ResponseEntity<List<String>> getColumnPreferences(
    ) {
        return ResponseEntity.ok(List.of("column1", "column2", "column3")); // Simulated response
    }
}