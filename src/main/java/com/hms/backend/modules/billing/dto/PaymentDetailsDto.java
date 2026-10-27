package com.hms.backend.modules.billing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentDetailsDto {
    private UUID invoiceId;
    private BigDecimal amount;
    private String paymentStatus;
    private String upiUrl;
    private String qrCodeBase64;
    private String merchantVpa;
    private String merchantName;

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getUpiUrl() {
        return upiUrl;
    }

    public void setUpiUrl(String upiUrl) {
        this.upiUrl = upiUrl;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }

    public String getMerchantVpa() {
        return merchantVpa;
    }

    public void setMerchantVpa(String merchantVpa) {
        this.merchantVpa = merchantVpa;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
