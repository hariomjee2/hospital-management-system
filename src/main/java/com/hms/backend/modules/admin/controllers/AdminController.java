package com.hms.backend.modules.admin.controllers;

import com.hms.backend.modules.admin.dto.AdminStatsResponseDto;
import com.hms.backend.modules.admin.dto.RegisterDoctorRequestDto;
import com.hms.backend.modules.admin.services.AdminService;
import com.hms.backend.modules.doctor.dto.DoctorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponseDto> getAdminStats() {
        return ResponseEntity.ok(adminService.getAdminStats());
    }

    @PostMapping("/doctors")
    public ResponseEntity<DoctorResponseDto> registerDoctor(@RequestBody RegisterDoctorRequestDto request) {
        return ResponseEntity.ok(adminService.registerDoctor(request));
    }
}
