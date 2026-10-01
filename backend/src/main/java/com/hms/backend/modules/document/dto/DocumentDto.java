package com.hms.backend.modules.document.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class DocumentDto {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private String documentName;
    private String contentType;
    private long fileSize;
    private ZonedDateTime createdAt;

    public DocumentDto() {}

    public DocumentDto(UUID id, UUID patientId, String patientName, String documentName, String contentType, long fileSize, ZonedDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.documentName = documentName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
