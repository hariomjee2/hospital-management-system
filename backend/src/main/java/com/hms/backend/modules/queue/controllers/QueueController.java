package com.hms.backend.modules.queue.controllers;

import com.hms.backend.modules.appointment.dto.AppointmentResponseDto;
import com.hms.backend.modules.auth.security.UserDetailsImpl;
import com.hms.backend.modules.queue.services.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/check-in/{appointmentId}")
    public ResponseEntity<AppointmentResponseDto> checkIn(@PathVariable UUID appointmentId) {
        return ResponseEntity.ok(queueService.checkIn(appointmentId));
    }

    @PostMapping("/call-next")
    public ResponseEntity<AppointmentResponseDto> callNext() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppointmentResponseDto next = queueService.callNext(userDetails.getId());
        if (next == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(next);
    }

    @GetMapping("/doctor-live")
    public ResponseEntity<List<AppointmentResponseDto>> getDoctorQueue() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(queueService.getDoctorQueue(userDetails.getId()));
    }

    @GetMapping("/my-token")
    public ResponseEntity<Map<String, Object>> getMyQueueStatus() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(queueService.getMyQueueStatus(userDetails.getId()));
    }
}
