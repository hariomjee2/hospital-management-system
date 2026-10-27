package com.hms.backend.modules.appointment.services;

import com.hms.backend.modules.appointment.dto.AppointmentResponseDto;
import com.hms.backend.modules.appointment.dto.BookAppointmentRequestDto;
import com.hms.backend.modules.appointment.entities.Appointment;
import com.hms.backend.modules.appointment.repositories.AppointmentRepository;
import com.hms.backend.modules.auth.entities.User;
import com.hms.backend.modules.auth.repositories.UserRepository;
import com.hms.backend.modules.billing.entities.Invoice;
import com.hms.backend.modules.billing.repositories.InvoiceRepository;
import com.hms.backend.modules.doctor.entities.Doctor;
import com.hms.backend.modules.doctor.repositories.DoctorRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            UserRepository userRepository,
            InvoiceRepository invoiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public AppointmentResponseDto bookAppointment(UUID userId, BookAppointmentRequestDto dto) {
        // Auto-provision Patient profile if user does not have one yet
        Patient patient = patientRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found for ID: " + userId));
            Patient newPatient = new Patient();
            newPatient.setUser(user);
            String emailName = (user.getEmail() != null && user.getEmail().contains("@"))
                    ? user.getEmail().split("@")[0] : "Patient";
            newPatient.setFirstName(Character.toUpperCase(emailName.charAt(0)) + emailName.substring(1));
            newPatient.setLastName("User");
            newPatient.setBloodGroup("O+");
            newPatient.setEmergencyContact("555-0199");
            return patientRepository.save(newPatient);
        });

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found for ID: " + dto.getDoctorId()));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setScheduledTime(dto.getScheduledTime() != null ? dto.getScheduledTime() : ZonedDateTime.now().plusDays(1));
        appointment.setNotes(dto.getNotes());
        appointment.setStatus("REQUESTED");
        appointment.setQueueStatus("NOT_CHECKED_IN");

        Appointment saved = appointmentRepository.save(appointment);

        // Auto-generate consultation fee invoice
        Invoice invoice = new Invoice();
        invoice.setAppointment(saved);
        invoice.setPatient(patient);
        invoice.setAmount(doctor.getConsultationFee() != null ? doctor.getConsultationFee() : BigDecimal.valueOf(150));
        invoice.setPaymentStatus("UNPAID");
        invoiceRepository.save(invoice);

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

    @Transactional
    public AppointmentResponseDto referForIpd(UUID appointmentId, String wardType, String referralNotes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));

        appointment.setQueueStatus("RECOMMENDED_IPD");
        appointment.setRecommendedWardType(wardType != null ? wardType.toUpperCase() : "GENERAL");
        appointment.setIpdReferralNotes(referralNotes);

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
        dto.setRecommendedWardType(appt.getRecommendedWardType());
        dto.setIpdReferralNotes(appt.getIpdReferralNotes());
        dto.setNotes(appt.getNotes());
        return dto;
    }
}
