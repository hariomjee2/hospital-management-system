package com.hms.backend.modules.pharmacy.repositories;

import com.hms.backend.modules.pharmacy.entities.PharmacyDispensing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PharmacyDispensingRepository extends JpaRepository<PharmacyDispensing, UUID> {
    List<PharmacyDispensing> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
}
