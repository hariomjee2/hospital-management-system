package com.hms.backend.modules.pharmacy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class DispenseRequestDto {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    private UUID prescriptionId;

    @NotEmpty(message = "At least one medicine item is required")
    @Valid
    private List<DispenseItemDto> items;

    private String notes;

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(UUID prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public List<DispenseItemDto> getItems() {
        return items;
    }

    public void setItems(List<DispenseItemDto> items) {
        this.items = items;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
