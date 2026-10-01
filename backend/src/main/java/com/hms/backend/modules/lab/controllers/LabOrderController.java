package com.hms.backend.modules.lab.controllers;

import com.hms.backend.modules.auth.security.UserDetailsImpl;
import com.hms.backend.modules.lab.dto.AssignLabOrderRequestDto;
import com.hms.backend.modules.lab.dto.LabOrderResponseDto;
import com.hms.backend.modules.lab.entities.LabTestCatalog;
import com.hms.backend.modules.lab.services.LabOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class LabOrderController {

    private final LabOrderService labOrderService;

    public LabOrderController(LabOrderService labOrderService) {
        this.labOrderService = labOrderService;
    }

    @GetMapping("/lab-tests/catalog")
    public ResponseEntity<List<LabTestCatalog>> getCatalog(@RequestParam(value = "category", required = false) String category) {
        return ResponseEntity.ok(labOrderService.getCatalog(category));
    }

    @PostMapping("/lab-orders")
    public ResponseEntity<List<LabOrderResponseDto>> assignLabOrders(@RequestBody AssignLabOrderRequestDto request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(labOrderService.assignLabOrders(userDetails.getId(), request));
    }

    @GetMapping("/lab-orders/my")
    public ResponseEntity<List<LabOrderResponseDto>> getMyLabOrders() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(labOrderService.getMyLabOrders(userDetails.getId()));
    }

    @PatchMapping("/lab-orders/{id}/status")
    public ResponseEntity<LabOrderResponseDto> updateOrderStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String resultSummary = body.get("resultSummary");
        String doctorNotes = body.get("doctorNotes");

        return ResponseEntity.ok(labOrderService.updateOrderStatus(id, status, resultSummary, doctorNotes));
    }
}
