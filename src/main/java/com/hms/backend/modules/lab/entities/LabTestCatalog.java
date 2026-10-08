package com.hms.backend.modules.lab.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "lab_test_catalog")
public class LabTestCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(name = "disease_category", nullable = false)
    private String diseaseCategory;

    @Column(name = "test_code", nullable = false, unique = true)
    private String testCode;

    @Column(name = "sample_required", nullable = false)
    private String sampleRequired;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(columnDefinition = "TEXT")
    private String description;

    public LabTestCatalog() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getDiseaseCategory() { return diseaseCategory; }
    public void setDiseaseCategory(String diseaseCategory) { this.diseaseCategory = diseaseCategory; }

    public String getTestCode() { return testCode; }
    public void setTestCode(String testCode) { this.testCode = testCode; }

    public String getSampleRequired() { return sampleRequired; }
    public void setSampleRequired(String sampleRequired) { this.sampleRequired = sampleRequired; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
