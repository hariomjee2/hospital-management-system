package com.hms.backend.modules.doctor.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class DoctorResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String department;
    private BigDecimal consultationFee;
    private String email;

    public DoctorResponseDto() {}

    public DoctorResponseDto(UUID id, String firstName, String lastName, String specialization, String department, BigDecimal consultationFee, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.department = department;
        this.consultationFee = consultationFee;
        this.email = email;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
