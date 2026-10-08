package com.hms.backend.modules.document.entities;

import com.hms.backend.modules.auth.entities.User;
import com.hms.backend.modules.patient.entities.Patient;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_documents")
public class PatientDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Lob
    @JdbcType(VarbinaryJdbcType.class)
    @Column(nullable = false)
    private byte[] data;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    public PatientDocument() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
}
