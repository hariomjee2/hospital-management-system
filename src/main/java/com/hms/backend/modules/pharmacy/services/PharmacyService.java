package com.hms.backend.modules.pharmacy.services;

import com.hms.backend.modules.billing.entities.Invoice;
import com.hms.backend.modules.billing.repositories.InvoiceRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import com.hms.backend.modules.pharmacy.dto.*;
import com.hms.backend.modules.pharmacy.entities.PharmacyDispensing;
import com.hms.backend.modules.pharmacy.entities.PharmacyDispensingItem;
import com.hms.backend.modules.pharmacy.entities.PharmacyMedicine;
import com.hms.backend.modules.pharmacy.repositories.PharmacyDispensingRepository;
import com.hms.backend.modules.pharmacy.repositories.PharmacyMedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PharmacyService {

    private final PharmacyMedicineRepository medicineRepository;
    private final PharmacyDispensingRepository dispensingRepository;
    private final PatientRepository patientRepository;
    private final InvoiceRepository invoiceRepository;

    public PharmacyService(
            PharmacyMedicineRepository medicineRepository,
            PharmacyDispensingRepository dispensingRepository,
            PatientRepository patientRepository,
            InvoiceRepository invoiceRepository) {
        this.medicineRepository = medicineRepository;
        this.dispensingRepository = dispensingRepository;
        this.patientRepository = patientRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public List<MedicineResponseDto> getAllMedicines(String category, String search) {
        List<PharmacyMedicine> list;
        if (category != null && !category.isEmpty()) {
            list = medicineRepository.findByCategory(category);
        } else if (search != null && !search.isEmpty()) {
            list = medicineRepository.findByMedicineNameContainingIgnoreCase(search);
        } else {
            list = medicineRepository.findAll();
        }

        return list.stream().map(this::mapMedicineToDto).collect(Collectors.toList());
    }

    public List<MedicineResponseDto> getInventoryAlerts() {
        LocalDate thresholdDate = LocalDate.now().plusDays(30);
        List<PharmacyMedicine> lowStock = medicineRepository.findLowStockMedicines();
        List<PharmacyMedicine> expiringSoon = medicineRepository.findByExpiryDateBefore(thresholdDate);

        List<PharmacyMedicine> combined = new ArrayList<>(lowStock);
        for (PharmacyMedicine m : expiringSoon) {
            if (combined.stream().noneMatch(existing -> existing.getId().equals(m.getId()))) {
                combined.add(m);
            }
        }

        return combined.stream().map(this::mapMedicineToDto).collect(Collectors.toList());
    }

    @Transactional
    public MedicineResponseDto addOrUpdateMedicine(MedicineRequestDto dto) {
        PharmacyMedicine medicine = new PharmacyMedicine();
        medicine.setMedicineName(dto.getMedicineName());
        medicine.setGenericName(dto.getGenericName());
        medicine.setCategory(dto.getCategory());
        medicine.setBatchNumber(dto.getBatchNumber());
        medicine.setStockQuantity(dto.getStockQuantity());
        medicine.setReorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 10);
        medicine.setUnitPrice(dto.getUnitPrice());
        medicine.setExpiryDate(dto.getExpiryDate());
        medicine.setManufacturer(dto.getManufacturer());

        PharmacyMedicine saved = medicineRepository.save(medicine);
        return mapMedicineToDto(saved);
    }

    @Transactional
    public DispenseResponseDto dispenseMedicines(DispenseRequestDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + dto.getPatientId()));

        PharmacyDispensing dispensing = new PharmacyDispensing();
        dispensing.setPatient(patient);
        dispensing.setPrescriptionId(dto.getPrescriptionId());
        dispensing.setNotes(dto.getNotes());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PharmacyDispensingItem> dispensingItems = new ArrayList<>();

        for (DispenseItemDto itemDto : dto.getItems()) {
            PharmacyMedicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found: " + itemDto.getMedicineId()));

            if (medicine.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + medicine.getMedicineName() +
                        ". Available: " + medicine.getStockQuantity() + ", Requested: " + itemDto.getQuantity());
            }

            // Deduct Inventory Stock
            medicine.setStockQuantity(medicine.getStockQuantity() - itemDto.getQuantity());
            medicineRepository.save(medicine);

            BigDecimal itemSubtotal = medicine.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            totalAmount = totalAmount.add(itemSubtotal);

            PharmacyDispensingItem dispensingItem = new PharmacyDispensingItem();
            dispensingItem.setDispensing(dispensing);
            dispensingItem.setMedicine(medicine);
            dispensingItem.setQuantity(itemDto.getQuantity());
            dispensingItem.setUnitPrice(medicine.getUnitPrice());
            dispensingItem.setSubtotal(itemSubtotal);

            dispensingItems.add(dispensingItem);
        }

        dispensing.setTotalAmount(totalAmount);
        dispensing.setItems(dispensingItems);

        // Auto-create Pharmacy Invoice for Billing & Payment Checkout
        Invoice invoice = new Invoice();
        invoice.setPatient(patient);
        invoice.setAmount(totalAmount);
        invoice.setPaymentStatus("UNPAID");
        Invoice savedInvoice = invoiceRepository.save(invoice);

        dispensing.setInvoiceId(savedInvoice.getId());
        PharmacyDispensing savedDispensing = dispensingRepository.save(dispensing);

        return mapDispensingToDto(savedDispensing);
    }

    private MedicineResponseDto mapMedicineToDto(PharmacyMedicine m) {
        MedicineResponseDto dto = new MedicineResponseDto();
        dto.setId(m.getId());
        dto.setMedicineName(m.getMedicineName());
        dto.setGenericName(m.getGenericName());
        dto.setCategory(m.getCategory());
        dto.setBatchNumber(m.getBatchNumber());
        dto.setStockQuantity(m.getStockQuantity());
        dto.setReorderLevel(m.getReorderLevel());
        dto.setUnitPrice(m.getUnitPrice());
        dto.setExpiryDate(m.getExpiryDate());
        dto.setManufacturer(m.getManufacturer());

        dto.setLowStock(m.getStockQuantity() <= m.getReorderLevel());
        dto.setExpiringSoon(m.getExpiryDate() != null && m.getExpiryDate().isBefore(LocalDate.now().plusDays(30)));

        return dto;
    }

    private DispenseResponseDto mapDispensingToDto(PharmacyDispensing d) {
        DispenseResponseDto dto = new DispenseResponseDto();
        dto.setDispensingId(d.getId());
        if (d.getPatient() != null) {
            dto.setPatientId(d.getPatient().getId());
            dto.setPatientName(d.getPatient().getFirstName() + " " + d.getPatient().getLastName());
        }
        dto.setPrescriptionId(d.getPrescriptionId());
        dto.setTotalAmount(d.getTotalAmount());
        dto.setPaymentStatus(d.getPaymentStatus());
        dto.setInvoiceId(d.getInvoiceId());
        dto.setNotes(d.getNotes());
        dto.setCreatedAt(d.getCreatedAt());

        if (d.getItems() != null) {
            List<DispenseResponseDto.DispensedItemResponseDto> itemDtos = d.getItems().stream().map(item -> {
                DispenseResponseDto.DispensedItemResponseDto itemDto = new DispenseResponseDto.DispensedItemResponseDto();
                itemDto.setMedicineId(item.getMedicine().getId());
                itemDto.setMedicineName(item.getMedicine().getMedicineName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setSubtotal(item.getSubtotal());
                return itemDto;
            }).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }

        return dto;
    }
}
