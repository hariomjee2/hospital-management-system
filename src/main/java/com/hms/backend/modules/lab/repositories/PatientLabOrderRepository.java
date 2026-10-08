package com.hms.backend.modules.lab.repositories;

import com.hms.backend.modules.lab.entities.PatientLabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientLabOrderRepository extends JpaRepository<PatientLabOrder, UUID> {
    List<PatientLabOrder> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    List<PatientLabOrder> findByDoctorIdOrderByCreatedAtDesc(UUID doctorId);
}
