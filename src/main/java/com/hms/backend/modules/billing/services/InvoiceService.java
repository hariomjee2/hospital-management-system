package com.hms.backend.modules.billing.services;

import com.hms.backend.modules.billing.dto.InvoiceResponseDto;
import com.hms.backend.modules.billing.entities.Invoice;
import com.hms.backend.modules.billing.repositories.InvoiceRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, PatientRepository patientRepository) {
        this.invoiceRepository = invoiceRepository;
        this.patientRepository = patientRepository;
    }

    public List<InvoiceResponseDto> getMyInvoices(UUID userId) {
        Optional<Patient> patientOpt = patientRepository.findByUserId(userId);
        if (patientOpt.isPresent()) {
            return invoiceRepository.findByPatientIdOrderByCreatedAtDesc(patientOpt.get().getId())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return invoiceRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public InvoiceResponseDto payInvoice(UUID invoiceId, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        invoice.setPaymentStatus("PAID");
        invoice.setPaymentMethod(paymentMethod != null ? paymentMethod.toUpperCase() : "CREDIT_CARD");
        invoice.setTransactionRef("TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        invoice.setPaidAt(ZonedDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return mapToDto(saved);
    }

    private InvoiceResponseDto mapToDto(Invoice inv) {
        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setId(inv.getId());
        if (inv.getAppointment() != null) {
            dto.setAppointmentId(inv.getAppointment().getId());
            if (inv.getAppointment().getDoctor() != null) {
                dto.setDoctorName("Dr. " + inv.getAppointment().getDoctor().getFirstName() + " " + inv.getAppointment().getDoctor().getLastName());
            }
        }
        dto.setAmount(inv.getAmount());
        dto.setPaymentStatus(inv.getPaymentStatus());
        dto.setPaymentMethod(inv.getPaymentMethod());
        dto.setTransactionRef(inv.getTransactionRef());
        dto.setPaidAt(inv.getPaidAt());
        dto.setCreatedAt(inv.getCreatedAt());
        return dto;
    }
}
