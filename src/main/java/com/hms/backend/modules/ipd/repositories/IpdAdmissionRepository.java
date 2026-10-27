package com.hms.backend.modules.ipd.repositories;

import com.hms.backend.modules.ipd.entities.IpdAdmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IpdAdmissionRepository extends JpaRepository<IpdAdmission, UUID> {
    List<IpdAdmission> findByPatientIdOrderByAdmissionDateDesc(UUID patientId);
    List<IpdAdmission> findByDoctorIdOrderByAdmissionDateDesc(UUID doctorId);
    List<IpdAdmission> findByStatusOrderByAdmissionDateDesc(String status);
    Optional<IpdAdmission> findByPatientIdAndStatus(UUID patientId, String status);
}
