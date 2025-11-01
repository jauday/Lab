package com.lab.patientservice.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserColumnPreference extends BaseEntity {
    @Id
    private Long id;

    private String userId;
    private String tableName; // 'analysis', 'patients', etc.

    @ElementCollection
    private List<String> visibleColumns;

    // getters, setters...
}