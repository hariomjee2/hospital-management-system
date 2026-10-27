package com.hms.backend.modules.pharmacy.controllers;

import com.hms.backend.modules.pharmacy.dto.*;
import com.hms.backend.modules.pharmacy.services.PharmacyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacy")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping("/medicines")
    public ResponseEntity<List<MedicineResponseDto>> getAllMedicines(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(pharmacyService.getAllMedicines(category, search));
    }

    @PostMapping("/medicines")
    public ResponseEntity<MedicineResponseDto> addOrUpdateMedicine(@Valid @RequestBody MedicineRequestDto dto) {
        return ResponseEntity.ok(pharmacyService.addOrUpdateMedicine(dto));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<MedicineResponseDto>> getInventoryAlerts() {
        return ResponseEntity.ok(pharmacyService.getInventoryAlerts());
    }

    @PostMapping("/dispense")
    public ResponseEntity<DispenseResponseDto> dispenseMedicines(@Valid @RequestBody DispenseRequestDto dto) {
        return ResponseEntity.ok(pharmacyService.dispenseMedicines(dto));
    }
}
