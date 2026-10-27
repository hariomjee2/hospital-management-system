package com.hms.backend.modules.ipd.controllers;

import com.hms.backend.modules.auth.security.UserDetailsImpl;
import com.hms.backend.modules.ipd.dto.*;
import com.hms.backend.modules.ipd.services.IpdService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ipd")
public class IpdController {

    private final IpdService ipdService;

    public IpdController(IpdService ipdService) {
        this.ipdService = ipdService;
    }

    @GetMapping("/wards")
    public ResponseEntity<List<WardResponseDto>> getAllWards() {
        return ResponseEntity.ok(ipdService.getAllWards());
    }

    @GetMapping("/beds")
    public ResponseEntity<List<BedResponseDto>> getBeds(
            @RequestParam(required = false) UUID wardId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ipdService.getBeds(wardId, status));
    }

    @PostMapping("/admissions")
    public ResponseEntity<AdmissionResponseDto> admitPatient(@Valid @RequestBody AdmissionRequestDto dto) {
        return ResponseEntity.ok(ipdService.admitPatient(dto));
    }

    @PostMapping("/admissions/{id}/discharge")
    public ResponseEntity<AdmissionResponseDto> dischargePatient(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        String dischargeSummary = (body != null && body.containsKey("dischargeSummary"))
                ? body.get("dischargeSummary")
                : "Patient recovered and discharged in stable condition.";
        return ResponseEntity.ok(ipdService.dischargePatient(id, dischargeSummary));
    }

    @GetMapping("/admissions")
    public ResponseEntity<List<AdmissionResponseDto>> getAllAdmissions(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ipdService.getAllAdmissions(status));
    }

    @GetMapping("/admissions/my")
    public ResponseEntity<AdmissionResponseDto> getMyActiveAdmission() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ipdService.getActivePatientAdmission(userDetails.getId()));
    }
}
