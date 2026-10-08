package com.hms.backend.modules.lab.services;

import com.hms.backend.modules.appointment.entities.Appointment;
import com.hms.backend.modules.appointment.repositories.AppointmentRepository;
import com.hms.backend.modules.doctor.entities.Doctor;
import com.hms.backend.modules.doctor.repositories.DoctorRepository;
import com.hms.backend.modules.lab.dto.AssignLabOrderRequestDto;
import com.hms.backend.modules.lab.dto.LabOrderResponseDto;
import com.hms.backend.modules.lab.entities.LabTestCatalog;
import com.hms.backend.modules.lab.entities.PatientLabOrder;
import com.hms.backend.modules.lab.repositories.LabTestCatalogRepository;
import com.hms.backend.modules.lab.repositories.PatientLabOrderRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LabOrderService {

    private final LabTestCatalogRepository catalogRepository;
    private final PatientLabOrderRepository orderRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public LabOrderService(LabTestCatalogRepository catalogRepository,
                           PatientLabOrderRepository orderRepository,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository) {
        this.catalogRepository = catalogRepository;
        this.orderRepository = orderRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<LabTestCatalog> getCatalog(String category) {
        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            return catalogRepository.findByDiseaseCategoryIgnoreCase(category.trim());
        }
        return catalogRepository.findAll();
    }

    public List<LabOrderResponseDto> assignLabOrders(UUID doctorUserId, AssignLabOrderRequestDto dto) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user ID: " + doctorUserId));

        Patient patient;
        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(dto.getAppointmentId()).orElse(null);
            if (appointment != null) {
                patient = appointment.getPatient();
            } else {
                patient = patientRepository.findById(dto.getPatientId())
                        .orElseThrow(() -> new RuntimeException("Patient not found: " + dto.getPatientId()));
            }
        } else {
            patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found: " + dto.getPatientId()));
        }

        List<PatientLabOrder> createdOrders = new ArrayList<>();
        if (dto.getTestIds() != null) {
            for (UUID testId : dto.getTestIds()) {
                LabTestCatalog test = catalogRepository.findById(testId)
                        .orElseThrow(() -> new RuntimeException("Lab test not found in catalog: " + testId));

                PatientLabOrder order = new PatientLabOrder();
                order.setAppointment(appointment);
                order.setPatient(patient);
                order.setDoctor(doctor);
                order.setTest(test);
                order.setDiseaseCondition(dto.getDiseaseCondition() != null ? dto.getDiseaseCondition() : test.getDiseaseCategory());
                order.setStatus("ORDERED");
                order.setDoctorNotes(dto.getDoctorNotes());

                createdOrders.add(orderRepository.save(order));
            }
        }

        return createdOrders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<LabOrderResponseDto> getMyLabOrders(UUID userId) {
        Optional<Patient> patientOpt = patientRepository.findByUserId(userId);
        if (patientOpt.isPresent()) {
            return orderRepository.findByPatientIdOrderByCreatedAtDesc(patientOpt.get().getId())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        }

        Optional<Doctor> doctorOpt = doctorRepository.findByUserId(userId);
        if (doctorOpt.isPresent()) {
            return orderRepository.findByDoctorIdOrderByCreatedAtDesc(doctorOpt.get().getId())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        }

        return orderRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public LabOrderResponseDto updateOrderStatus(UUID orderId, String status, String resultSummary, String doctorNotes) {
        PatientLabOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Lab order not found: " + orderId));

        order.setStatus(status.toUpperCase());
        if (resultSummary != null) {
            order.setResultSummary(resultSummary);
        }
        if (doctorNotes != null) {
            order.setDoctorNotes(doctorNotes);
        }

        PatientLabOrder saved = orderRepository.save(order);
        return mapToDto(saved);
    }

    private LabOrderResponseDto mapToDto(PatientLabOrder order) {
        LabOrderResponseDto dto = new LabOrderResponseDto();
        dto.setId(order.getId());
        if (order.getAppointment() != null) {
            dto.setAppointmentId(order.getAppointment().getId());
        }
        dto.setPatientName(order.getPatient().getFirstName() + " " + order.getPatient().getLastName());
        dto.setDoctorName("Dr. " + order.getDoctor().getFirstName() + " " + order.getDoctor().getLastName());
        dto.setTestName(order.getTest().getTestName());
        dto.setTestCode(order.getTest().getTestCode());
        dto.setDiseaseCategory(order.getTest().getDiseaseCategory());
        dto.setSampleRequired(order.getTest().getSampleRequired());
        dto.setCost(order.getTest().getCost());
        dto.setStatus(order.getStatus());
        dto.setDoctorNotes(order.getDoctorNotes());
        dto.setResultSummary(order.getResultSummary());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
