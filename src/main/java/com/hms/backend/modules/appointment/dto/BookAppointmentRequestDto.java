package com.hms.backend.modules.appointment.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class BookAppointmentRequestDto {
    private UUID doctorId;
    private ZonedDateTime scheduledTime;
    private String notes;

    public BookAppointmentRequestDto() {}

    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }

    public ZonedDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(ZonedDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
