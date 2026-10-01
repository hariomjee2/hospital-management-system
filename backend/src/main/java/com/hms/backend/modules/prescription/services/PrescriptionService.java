package com.hms.backend.modules.prescription.services;

import com.hms.backend.modules.appointment.entities.Appointment;
import com.hms.backend.modules.appointment.repositories.AppointmentRepository;
import com.hms.backend.modules.billing.entities.Invoice;
import com.hms.backend.modules.billing.repositories.InvoiceRepository;
import com.hms.backend.modules.doctor.entities.Doctor;
import com.hms.backend.modules.doctor.repositories.DoctorRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import com.hms.backend.modules.prescription.dto.CreatePrescriptionRequestDto;
import com.hms.backend.modules.prescription.dto.PrescriptionResponseDto;
import com.hms.backend.modules.prescription.entities.Prescription;
import com.hms.backend.modules.prescription.entities.PrescriptionItem;
import com.hms.backend.modules.prescription.repositories.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               AppointmentRepository appointmentRepository,
                               InvoiceRepository invoiceRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public PrescriptionResponseDto createPrescription(UUID doctorUserId, CreatePrescriptionRequestDto dto) {
        Appointment appt = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + dto.getAppointmentId()));

        Prescription prescription = new Prescription();
        prescription.setAppointment(appt);
        prescription.setPatient(appt.getPatient());
        prescription.setDoctor(appt.getDoctor());
        prescription.setDiagnosis(dto.getDiagnosis());
        prescription.setNotes(dto.getNotes());

        if (dto.getItems() != null) {
            for (CreatePrescriptionRequestDto.PrescriptionItemDto itemDto : dto.getItems()) {
                PrescriptionItem item = new PrescriptionItem(
                        itemDto.getMedicineName(),
                        itemDto.getDosage(),
                        itemDto.getInstructions()
                );
                prescription.addItem(item);
            }
        }

        Prescription saved = prescriptionRepository.save(prescription);

        // Update Appointment status
        appt.setStatus("COMPLETED");
        appt.setQueueStatus("COMPLETED");
        appointmentRepository.save(appt);

        // Auto-generate invoice for consultation
        Invoice invoice = new Invoice();
        invoice.setAppointment(appt);
        invoice.setPatient(appt.getPatient());
        invoice.setAmount(appt.getDoctor().getConsultationFee());
        invoice.setPaymentStatus("UNPAID");
        invoiceRepository.save(invoice);

        return mapToDto(saved);
    }

    public List<PrescriptionResponseDto> getMyPrescriptions(UUID userId) {
        Optional<Patient> patientOpt = patientRepository.findByUserId(userId);
        if (patientOpt.isPresent()) {
            return prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientOpt.get().getId())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        }

        Optional<Doctor> doctorOpt = doctorRepository.findByUserId(userId);
        if (doctorOpt.isPresent()) {
            return prescriptionRepository.findByDoctorIdOrderByCreatedAtDesc(doctorOpt.get().getId())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        }

        return prescriptionRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private PrescriptionResponseDto mapToDto(Prescription rx) {
        PrescriptionResponseDto dto = new PrescriptionResponseDto();
        dto.setId(rx.getId());
        dto.setAppointmentId(rx.getAppointment().getId());
        dto.setDoctorName("Dr. " + rx.getDoctor().getFirstName() + " " + rx.getDoctor().getLastName());
        dto.setDoctorSpecialization(rx.getDoctor().getSpecialization());
        dto.setPatientName(rx.getPatient().getFirstName() + " " + rx.getPatient().getLastName());
        dto.setDiagnosis(rx.getDiagnosis());
        dto.setNotes(rx.getNotes());
        dto.setCreatedAt(rx.getCreatedAt());

        List<PrescriptionResponseDto.ItemResponseDto> itemDtos = rx.getItems().stream()
                .map(item -> new PrescriptionResponseDto.ItemResponseDto(
                        item.getId(),
                        item.getMedicineName(),
                        item.getDosage(),
                        item.getInstructions()
                )).collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}
