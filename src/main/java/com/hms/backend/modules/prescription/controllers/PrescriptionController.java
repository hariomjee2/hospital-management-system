package com.hms.backend.modules.prescription.controllers;

import com.hms.backend.modules.auth.security.UserDetailsImpl;
import com.hms.backend.modules.prescription.dto.CreatePrescriptionRequestDto;
import com.hms.backend.modules.prescription.dto.PrescriptionResponseDto;
import com.hms.backend.modules.prescription.services.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<PrescriptionResponseDto> createPrescription(@RequestBody CreatePrescriptionRequestDto request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(prescriptionService.createPrescription(userDetails.getId(), request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PrescriptionResponseDto>> getMyPrescriptions() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(prescriptionService.getMyPrescriptions(userDetails.getId()));
    }
}
