package com.hms.backend.modules.billing.controllers;

import com.hms.backend.modules.auth.security.UserDetailsImpl;
import com.hms.backend.modules.billing.dto.InvoiceResponseDto;
import com.hms.backend.modules.billing.dto.PaymentDetailsDto;
import com.hms.backend.modules.billing.services.InvoiceService;
import com.hms.backend.modules.billing.services.PaymentQrService;
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
    private final PaymentQrService paymentQrService;

    public InvoiceController(InvoiceService invoiceService, PaymentQrService paymentQrService) {
        this.invoiceService = invoiceService;
        this.paymentQrService = paymentQrService;
    }

    @GetMapping("/my")
    public ResponseEntity<List<InvoiceResponseDto>> getMyInvoices() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(invoiceService.getMyInvoices(userDetails.getId()));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponseDto> payInvoice(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String paymentMethod = (body != null) ? body.get("paymentMethod") : "UPI_QR";
        return ResponseEntity.ok(invoiceService.payInvoice(id, paymentMethod));
    }

    @GetMapping("/{id}/payment-details")
    public ResponseEntity<PaymentDetailsDto> getPaymentDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentQrService.generateInvoicePaymentDetails(id));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> getInvoiceStatus(@PathVariable UUID id) {
        InvoiceResponseDto dto = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(Map.of("status", dto.getPaymentStatus(), "invoiceId", id.toString()));
    }

    @PostMapping("/{id}/verify-payment")
    public ResponseEntity<InvoiceResponseDto> verifyPayment(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String utr = (body != null && body.containsKey("utr")) ? body.get("utr") : "UPI_" + System.currentTimeMillis();
        return ResponseEntity.ok(invoiceService.verifyAndPayInvoice(id, "UPI_QR", utr));
    }
}
