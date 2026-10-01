package com.hms.backend.modules.prescription.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class PrescriptionResponseDto {
    private UUID id;
    private UUID appointmentId;
    private String doctorName;
    private String doctorSpecialization;
    private String patientName;
    private String diagnosis;
    private String notes;
    private List<ItemResponseDto> items;
    private ZonedDateTime createdAt;

    public static class ItemResponseDto {
        private UUID id;
        private String medicineName;
        private String dosage;
        private String instructions;

        public ItemResponseDto() {}

        public ItemResponseDto(UUID id, String medicineName, String dosage, String instructions) {
            this.id = id;
            this.medicineName = medicineName;
            this.dosage = dosage;
            this.instructions = instructions;
        }

        public UUID getId() { return id; }
        public String getMedicineName() { return medicineName; }
        public String getDosage() { return dosage; }
        public String getInstructions() { return instructions; }
    }

    public PrescriptionResponseDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorSpecialization() { return doctorSpecialization; }
    public void setDoctorSpecialization(String doctorSpecialization) { this.doctorSpecialization = doctorSpecialization; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<ItemResponseDto> getItems() { return items; }
    public void setItems(List<ItemResponseDto> items) { this.items = items; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
