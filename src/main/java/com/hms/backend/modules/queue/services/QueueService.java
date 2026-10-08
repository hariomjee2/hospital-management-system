package com.hms.backend.modules.queue.services;

import com.hms.backend.modules.appointment.dto.AppointmentResponseDto;
import com.hms.backend.modules.appointment.entities.Appointment;
import com.hms.backend.modules.appointment.repositories.AppointmentRepository;
import com.hms.backend.modules.appointment.services.AppointmentService;
import com.hms.backend.modules.doctor.entities.Doctor;
import com.hms.backend.modules.doctor.repositories.DoctorRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentService appointmentService;

    public QueueService(AppointmentRepository appointmentRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository,
                        AppointmentService appointmentService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentService = appointmentService;
    }

    public AppointmentResponseDto checkIn(UUID appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));

        if (appt.getTokenNumber() != null) {
            return appointmentService.mapToDto(appt);
        }

        UUID doctorId = appt.getDoctor().getId();
        List<Appointment> doctorAppts = appointmentRepository.findByDoctorIdOrderByScheduledTimeDesc(doctorId);

        int maxToken = doctorAppts.stream()
                .map(Appointment::getTokenNumber)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        appt.setTokenNumber(maxToken + 1);
        appt.setQueueStatus("WAITING");
        appt.setStatus("CONFIRMED");
        appt.setCheckInTime(ZonedDateTime.now());

        Appointment saved = appointmentRepository.save(appt);
        return appointmentService.mapToDto(saved);
    }

    public AppointmentResponseDto callNext(UUID doctorUserId) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user ID: " + doctorUserId));

        List<Appointment> doctorAppts = appointmentRepository.findByDoctorIdOrderByScheduledTimeDesc(doctor.getId());

        // Complete current IN_CABIN patient
        doctorAppts.stream()
                .filter(a -> "IN_CABIN".equalsIgnoreCase(a.getQueueStatus()))
                .forEach(a -> {
                    a.setQueueStatus("COMPLETED");
                    a.setStatus("COMPLETED");
                    appointmentRepository.save(a);
                });

        // Find lowest WAITING token
        Optional<Appointment> nextWaiting = doctorAppts.stream()
                .filter(a -> "WAITING".equalsIgnoreCase(a.getQueueStatus()))
                .min(Comparator.comparingInt(Appointment::getTokenNumber));

        if (nextWaiting.isPresent()) {
            Appointment next = nextWaiting.get();
            next.setQueueStatus("IN_CABIN");
            Appointment saved = appointmentRepository.save(next);
            return appointmentService.mapToDto(saved);
        }

        return null; // Queue is empty
    }

    public List<AppointmentResponseDto> getDoctorQueue(UUID doctorUserId) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user ID: " + doctorUserId));

        return appointmentRepository.findByDoctorIdOrderByScheduledTimeDesc(doctor.getId())
                .stream()
                .filter(a -> a.getTokenNumber() != null)
                .sorted(Comparator.comparingInt(Appointment::getTokenNumber))
                .map(appointmentService::mapToDto)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getMyQueueStatus(UUID patientUserId) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found for user ID: " + patientUserId));

        List<Appointment> patientAppts = appointmentRepository.findByPatientIdOrderByScheduledTimeDesc(patient.getId());

        Optional<Appointment> activeAppt = patientAppts.stream()
                .filter(a -> a.getTokenNumber() != null && !"COMPLETED".equalsIgnoreCase(a.getQueueStatus()))
                .findFirst();

        Map<String, Object> response = new HashMap<>();
        if (activeAppt.isPresent()) {
            Appointment appt = activeAppt.get();
            response.put("hasCheckIn", true);
            response.put("appointmentId", appt.getId());
            response.put("doctorName", "Dr. " + appt.getDoctor().getFirstName() + " " + appt.getDoctor().getLastName());
            response.put("myTokenNumber", appt.getTokenNumber());
            response.put("queueStatus", appt.getQueueStatus());

            // Find currently serving token number for this doctor
            List<Appointment> doctorAppts = appointmentRepository.findByDoctorIdOrderByScheduledTimeDesc(appt.getDoctor().getId());
            Optional<Appointment> inCabin = doctorAppts.stream()
                    .filter(a -> "IN_CABIN".equalsIgnoreCase(a.getQueueStatus()))
                    .findFirst();

            response.put("currentServingTokenNumber", inCabin.map(Appointment::getTokenNumber).orElse(0));
        } else {
            response.put("hasCheckIn", false);
        }

        return response;
    }
}
