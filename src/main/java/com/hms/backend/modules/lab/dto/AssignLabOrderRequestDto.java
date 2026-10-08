package com.hms.backend.modules.lab.dto;

import java.util.List;
import java.util.UUID;

public class AssignLabOrderRequestDto {
    private UUID appointmentId;
    private UUID patientId;
    private String diseaseCondition;
    private List<UUID> testIds;
    private String doctorNotes;

    public AssignLabOrderRequestDto() {}

    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }

    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }

    public String getDiseaseCondition() { return diseaseCondition; }
    public void setDiseaseCondition(String diseaseCondition) { this.diseaseCondition = diseaseCondition; }

    public List<UUID> getTestIds() { return testIds; }
    public void setTestIds(List<UUID> testIds) { this.testIds = testIds; }

    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }
}
