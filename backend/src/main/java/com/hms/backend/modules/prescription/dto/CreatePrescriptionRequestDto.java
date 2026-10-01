package com.hms.backend.modules.prescription.dto;

import java.util.List;
import java.util.UUID;

public class CreatePrescriptionRequestDto {
    private UUID appointmentId;
    private String diagnosis;
    private String notes;
    private List<PrescriptionItemDto> items;

    public static class PrescriptionItemDto {
        private String medicineName;
        private String dosage;
        private String instructions;

        public PrescriptionItemDto() {}

        public String getMedicineName() { return medicineName; }
        public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
    }

    public CreatePrescriptionRequestDto() {}

    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<PrescriptionItemDto> getItems() { return items; }
    public void setItems(List<PrescriptionItemDto> items) { this.items = items; }
}
