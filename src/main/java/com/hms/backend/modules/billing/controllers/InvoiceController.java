package com.hms.backend.modules.billing.controllers;

import com.hms.backend.modules.auth.security.UserDetailsImpl;
import com.hms.backend.modules.billing.dto.InvoiceResponseDto;
import com.hms.backend.modules.billing.services.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/my")
    public ResponseEntity<List<InvoiceResponseDto>> getMyInvoices() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(invoiceService.getMyInvoices(userDetails.getId()));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponseDto> payInvoice(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String paymentMethod = (body != null) ? body.get("paymentMethod") : "CREDIT_CARD";
        return ResponseEntity.ok(invoiceService.payInvoice(id, paymentMethod));
    }
}
