package com.hms.backend.modules.billing.repositories;

import com.hms.backend.modules.billing.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
}
