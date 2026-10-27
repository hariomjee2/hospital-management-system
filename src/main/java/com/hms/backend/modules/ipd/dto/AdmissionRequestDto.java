package com.hms.backend.modules.ipd.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class AdmissionRequestDto {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Attending Doctor ID is required")
    private UUID doctorId;

    @NotNull(message = "Assigned Bed ID is required")
    private UUID bedId;

    private String admissionNotes;

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }

    public UUID getBedId() {
        return bedId;
    }

    public void setBedId(UUID bedId) {
        this.bedId = bedId;
    }

    public String getAdmissionNotes() {
        return admissionNotes;
    }

    public void setAdmissionNotes(String admissionNotes) {
        this.admissionNotes = admissionNotes;
    }
}
