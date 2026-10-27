package com.hms.backend.modules.appointment.controllers;

import com.hms.backend.modules.appointment.dto.AppointmentResponseDto;
import com.hms.backend.modules.appointment.dto.BookAppointmentRequestDto;
import com.hms.backend.modules.appointment.services.AppointmentService;
import com.hms.backend.modules.auth.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<?> bookAppointment(@RequestBody BookAppointmentRequestDto request) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(appointmentService.bookAppointment(userDetails.getId(), request));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponseDto>> getMyAppointments() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(appointmentService.getMyAppointments(userDetails.getId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            return ResponseEntity.ok(appointmentService.updateStatus(id, status));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/refer-ipd")
    public ResponseEntity<?> referForIpd(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        try {
            String wardType = (body != null && body.containsKey("wardType")) ? body.get("wardType") : "GENERAL";
            String notes = (body != null && body.containsKey("notes")) ? body.get("notes") : "Recommended for IPD observation.";
            return ResponseEntity.ok(appointmentService.referForIpd(id, wardType, notes));
        } catch (Exception e) {
            System.err.println("Error in referForIpd: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}
