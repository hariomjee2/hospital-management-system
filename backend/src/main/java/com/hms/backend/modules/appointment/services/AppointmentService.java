package com.hms.backend.modules.appointment.services;

import com.hms.backend.modules.appointment.dto.AppointmentResponseDto;
import com.hms.backend.modules.appointment.dto.BookAppointmentRequestDto;
import com.hms.backend.modules.appointment.entities.Appointment;
import com.hms.backend.modules.appointment.repositories.AppointmentRepository;
import com.hms.backend.modules.doctor.entities.Doctor;
import com.hms.backend.modules.doctor.repositories.DoctorRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public AppointmentResponseDto bookAppointment(UUID userId, BookAppointmentRequestDto dto) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found for user ID: " + userId));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found for ID: " + dto.getDoctorId()));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setScheduledTime(dto.getScheduledTime());
        appointment.setNotes(dto.getNotes());
        appointment.setStatus("REQUESTED");
        appointment.setQueueStatus("NOT_CHECKED_IN");

        Appointment saved = appointmentRepository.save(appointment);
        return mapToDto(saved);
    }

    public List<AppointmentResponseDto> getMyAppointments(UUID userId) {
        Optional<Patient> patientOpt = patientRepository.findByUserId(userId);
        if (patientOpt.isPresent()) {
            return appointmentRepository.findByPatientIdOrderByScheduledTimeDesc(patientOpt.get().getId())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        }

        Optional<Doctor> doctorOpt = doctorRepository.findByUserId(userId);
        if (doctorOpt.isPresent()) {
            return appointmentRepository.findByDoctorIdOrderByScheduledTimeDesc(doctorOpt.get().getId())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        }

        return appointmentRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public AppointmentResponseDto updateStatus(UUID appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));

        appointment.setStatus(status.toUpperCase());
        Appointment saved = appointmentRepository.save(appointment);
        return mapToDto(saved);
    }

    public AppointmentResponseDto mapToDto(Appointment appt) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(appt.getId());
        if (appt.getPatient() != null) {
            dto.setPatientId(appt.getPatient().getId());
            dto.setPatientName(appt.getPatient().getFirstName() + " " + appt.getPatient().getLastName());
        }
        if (appt.getDoctor() != null) {
            dto.setDoctorId(appt.getDoctor().getId());
            dto.setDoctorName("Dr. " + appt.getDoctor().getFirstName() + " " + appt.getDoctor().getLastName());
            dto.setDoctorSpecialization(appt.getDoctor().getSpecialization());
        }
        dto.setScheduledTime(appt.getScheduledTime());
        dto.setStatus(appt.getStatus());
        dto.setTokenNumber(appt.getTokenNumber());
        dto.setQueueStatus(appt.getQueueStatus());
        dto.setCheckInTime(appt.getCheckInTime());
        dto.setNotes(appt.getNotes());
        return dto;
    }
}
