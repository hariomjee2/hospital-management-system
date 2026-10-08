package com.hms.backend.modules.doctor.services;

import com.hms.backend.modules.doctor.dto.DoctorResponseDto;
import com.hms.backend.modules.doctor.repositories.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepository.findAll().stream().map(doc -> new DoctorResponseDto(
                doc.getId(),
                doc.getFirstName(),
                doc.getLastName(),
                doc.getSpecialization(),
                doc.getDepartment(),
                doc.getConsultationFee(),
                doc.getUser() != null ? doc.getUser().getEmail() : ""
        )).collect(Collectors.toList());
    }
}
