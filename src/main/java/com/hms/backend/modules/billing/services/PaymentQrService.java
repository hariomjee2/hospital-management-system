package com.hms.backend.modules.billing.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hms.backend.modules.billing.dto.PaymentDetailsDto;
import com.hms.backend.modules.billing.entities.Invoice;
import com.hms.backend.modules.billing.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class PaymentQrService {

    private final InvoiceRepository invoiceRepository;

    @Value("${app.payment.merchant-vpa:hms.hospital@upi}")
    private String merchantVpa;

    @Value("${app.payment.merchant-name:HMS Care Hospital}")
    private String merchantName;

    public PaymentQrService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public PaymentDetailsDto generateInvoicePaymentDetails(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        BigDecimal amount = invoice.getAmount() != null ? invoice.getAmount() : BigDecimal.ZERO;
        String formattedAmount = String.format("%.2f", amount);
        String invRef = "INV-" + invoice.getId().toString().substring(0, 8).toUpperCase();

        // Build official NPCI UPI payment URL
        String upiUrl = String.format("upi://pay?pa=%s&pn=%s&tr=%s&am=%s&cu=INR&tn=%s",
                merchantVpa,
                URLEncoder.encode(merchantName, StandardCharsets.UTF_8),
                invRef,
                formattedAmount,
                URLEncoder.encode("Hospital Bill " + invRef, StandardCharsets.UTF_8)
        );

        String qrCodeBase64 = generateQrCodeBase64(upiUrl, 300, 300);

        PaymentDetailsDto dto = new PaymentDetailsDto();
        dto.setInvoiceId(invoice.getId());
        dto.setAmount(amount);
        dto.setPaymentStatus(invoice.getPaymentStatus());
        dto.setUpiUrl(upiUrl);
        dto.setQrCodeBase64(qrCodeBase64);
        dto.setMerchantVpa(merchantVpa);
        dto.setMerchantName(merchantName);

        return dto;
    }

    public String generateQrCodeBase64(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR Code: " + e.getMessage(), e);
        }
    }
}
