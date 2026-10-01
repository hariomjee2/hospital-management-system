package com.hms.backend.modules.document.repositories;

import com.hms.backend.modules.document.entities.PatientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientDocumentRepository extends JpaRepository<PatientDocument, UUID> {
    List<PatientDocument> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
}
