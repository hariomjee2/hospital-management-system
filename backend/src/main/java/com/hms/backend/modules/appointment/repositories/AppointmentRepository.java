package com.hms.backend.modules.appointment.repositories;

import com.hms.backend.modules.appointment.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByPatientIdOrderByScheduledTimeDesc(UUID patientId);
    List<Appointment> findByDoctorIdOrderByScheduledTimeDesc(UUID doctorId);
}
