package com.hms.backend.modules.prescription.repositories;

import com.hms.backend.modules.prescription.entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    List<Prescription> findByDoctorIdOrderByCreatedAtDesc(UUID doctorId);
}
