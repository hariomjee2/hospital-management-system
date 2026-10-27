package com.hms.backend.modules.ipd.services;

import com.hms.backend.modules.billing.entities.Invoice;
import com.hms.backend.modules.billing.repositories.InvoiceRepository;
import com.hms.backend.modules.doctor.entities.Doctor;
import com.hms.backend.modules.doctor.repositories.DoctorRepository;
import com.hms.backend.modules.ipd.dto.*;
import com.hms.backend.modules.ipd.entities.Bed;
import com.hms.backend.modules.ipd.entities.IpdAdmission;
import com.hms.backend.modules.ipd.entities.Ward;
import com.hms.backend.modules.ipd.repositories.BedRepository;
import com.hms.backend.modules.ipd.repositories.IpdAdmissionRepository;
import com.hms.backend.modules.ipd.repositories.WardRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IpdService {

    private final WardRepository wardRepository;
    private final BedRepository bedRepository;
    private final IpdAdmissionRepository ipdAdmissionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final InvoiceRepository invoiceRepository;

    public IpdService(
            WardRepository wardRepository,
            BedRepository bedRepository,
            IpdAdmissionRepository ipdAdmissionRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            InvoiceRepository invoiceRepository) {
        this.wardRepository = wardRepository;
        this.bedRepository = bedRepository;
        this.ipdAdmissionRepository = ipdAdmissionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public List<WardResponseDto> getAllWards() {
        return wardRepository.findAll().stream().map(ward -> {
            List<Bed> beds = bedRepository.findByWardId(ward.getId());
            long available = beds.stream().filter(b -> "AVAILABLE".equalsIgnoreCase(b.getStatus())).count();
            long occupied = beds.stream().filter(b -> "OCCUPIED".equalsIgnoreCase(b.getStatus())).count();

            WardResponseDto dto = new WardResponseDto();
            dto.setId(ward.getId());
            dto.setWardName(ward.getWardName());
            dto.setWardType(ward.getWardType());
            dto.setDailyRate(ward.getDailyRate());
            dto.setTotalBeds(ward.getTotalBeds());
            dto.setAvailableBedsCount(available);
            dto.setOccupiedBedsCount(occupied);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<BedResponseDto> getBeds(UUID wardId, String status) {
        List<Bed> beds;
        if (wardId != null && status != null) {
            beds = bedRepository.findByWardIdAndStatus(wardId, status.toUpperCase());
        } else if (wardId != null) {
            beds = bedRepository.findByWardId(wardId);
        } else if (status != null) {
            beds = bedRepository.findByStatus(status.toUpperCase());
        } else {
            beds = bedRepository.findAll();
        }

        return beds.stream().map(this::mapBedToDto).collect(Collectors.toList());
    }

    @Transactional
    public AdmissionResponseDto admitPatient(AdmissionRequestDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + dto.getDoctorId()));

        Bed bed = bedRepository.findById(dto.getBedId())
                .orElseThrow(() -> new RuntimeException("Bed not found: " + dto.getBedId()));

        if (!"AVAILABLE".equalsIgnoreCase(bed.getStatus())) {
            throw new RuntimeException("Selected bed " + bed.getBedNumber() + " is currently " + bed.getStatus());
        }

        // Lock bed status
        bed.setStatus("OCCUPIED");
        bedRepository.save(bed);

        IpdAdmission admission = new IpdAdmission();
        admission.setPatient(patient);
        admission.setDoctor(doctor);
        admission.setBed(bed);
        admission.setAdmissionDate(ZonedDateTime.now());
        admission.setAdmissionNotes(dto.getAdmissionNotes());
        admission.setStatus("ADMITTED");

        IpdAdmission saved = ipdAdmissionRepository.save(admission);
        return mapAdmissionToDto(saved, null);
    }

    @Transactional
    public AdmissionResponseDto dischargePatient(UUID admissionId, String dischargeSummary) {
        IpdAdmission admission = ipdAdmissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission record not found: " + admissionId));

        if ("DISCHARGED".equalsIgnoreCase(admission.getStatus())) {
            throw new RuntimeException("Patient is already discharged.");
        }

        ZonedDateTime dischargeTime = ZonedDateTime.now();
        admission.setDischargeDate(dischargeTime);
        admission.setDischargeSummary(dischargeSummary);
        admission.setStatus("DISCHARGED");

        // Calculate stay duration (minimum 1 day charge)
        long hoursStayed = Duration.between(admission.getAdmissionDate(), dischargeTime).toHours();
        long daysStayed = Math.max(1, (hoursStayed + 23) / 24); // ceil division

        BigDecimal dailyRate = admission.getBed().getWard().getDailyRate();
        BigDecimal totalBillAmount = dailyRate.multiply(BigDecimal.valueOf(daysStayed));
        admission.setTotalBillAmount(totalBillAmount);

        // Release bed
        Bed bed = admission.getBed();
        bed.setStatus("AVAILABLE");
        bedRepository.save(bed);

        IpdAdmission saved = ipdAdmissionRepository.save(admission);

        // Auto-generate IPD Hospital Bill Invoice
        Invoice invoice = new Invoice();
        invoice.setPatient(admission.getPatient());
        invoice.setAmount(totalBillAmount);
        invoice.setPaymentStatus("UNPAID");
        Invoice savedInvoice = invoiceRepository.save(invoice);

        return mapAdmissionToDto(saved, savedInvoice.getId());
    }

    public List<AdmissionResponseDto> getAllAdmissions(String status) {
        List<IpdAdmission> admissions;
        if (status != null && !status.isEmpty()) {
            admissions = ipdAdmissionRepository.findByStatusOrderByAdmissionDateDesc(status.toUpperCase());
        } else {
            admissions = ipdAdmissionRepository.findAll();
        }
        return admissions.stream().map(a -> mapAdmissionToDto(a, null)).collect(Collectors.toList());
    }

    public AdmissionResponseDto getActivePatientAdmission(UUID userId) {
        Optional<Patient> patientOpt = patientRepository.findByUserId(userId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient profile not found.");
        }

        Optional<IpdAdmission> admissionOpt = ipdAdmissionRepository.findByPatientIdAndStatus(patientOpt.get().getId(), "ADMITTED");
        return admissionOpt.map(a -> mapAdmissionToDto(a, null)).orElse(null);
    }

    private BedResponseDto mapBedToDto(Bed bed) {
        BedResponseDto dto = new BedResponseDto();
        dto.setId(bed.getId());
        if (bed.getWard() != null) {
            dto.setWardId(bed.getWard().getId());
            dto.setWardName(bed.getWard().getWardName());
            dto.setWardType(bed.getWard().getWardType());
            dto.setDailyRate(bed.getWard().getDailyRate());
        }
        dto.setBedNumber(bed.getBedNumber());
        dto.setStatus(bed.getStatus());
        return dto;
    }

    private AdmissionResponseDto mapAdmissionToDto(IpdAdmission a, UUID invoiceId) {
        AdmissionResponseDto dto = new AdmissionResponseDto();
        dto.setId(a.getId());
        if (a.getPatient() != null) {
            dto.setPatientId(a.getPatient().getId());
            dto.setPatientName(a.getPatient().getFirstName() + " " + a.getPatient().getLastName());
        }
        if (a.getDoctor() != null) {
            dto.setDoctorId(a.getDoctor().getId());
            dto.setDoctorName("Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName());
        }
        if (a.getBed() != null) {
            dto.setBedId(a.getBed().getId());
            dto.setBedNumber(a.getBed().getBedNumber());
            if (a.getBed().getWard() != null) {
                dto.setWardName(a.getBed().getWard().getWardName());
                dto.setDailyRate(a.getBed().getWard().getDailyRate());
            }
        }
        dto.setAdmissionDate(a.getAdmissionDate());
        dto.setDischargeDate(a.getDischargeDate());
        dto.setAdmissionNotes(a.getAdmissionNotes());
        dto.setDischargeSummary(a.getDischargeSummary());
        dto.setStatus(a.getStatus());
        dto.setTotalBillAmount(a.getTotalBillAmount());
        dto.setInvoiceId(invoiceId);
        return dto;
    }
}
