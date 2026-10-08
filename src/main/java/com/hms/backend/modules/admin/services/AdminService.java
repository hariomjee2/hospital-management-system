package com.hms.backend.modules.admin.services;

import com.hms.backend.modules.admin.dto.AdminStatsResponseDto;
import com.hms.backend.modules.admin.dto.RegisterDoctorRequestDto;
import com.hms.backend.modules.appointment.repositories.AppointmentRepository;
import com.hms.backend.modules.auth.entities.Role;
import com.hms.backend.modules.auth.entities.User;
import com.hms.backend.modules.auth.enums.RoleName;
import com.hms.backend.modules.auth.repositories.RoleRepository;
import com.hms.backend.modules.auth.repositories.UserRepository;
import com.hms.backend.modules.billing.entities.Invoice;
import com.hms.backend.modules.billing.repositories.InvoiceRepository;
import com.hms.backend.modules.doctor.dto.DoctorResponseDto;
import com.hms.backend.modules.doctor.entities.Doctor;
import com.hms.backend.modules.doctor.repositories.DoctorRepository;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(PatientRepository patientRepository,
                        DoctorRepository doctorRepository,
                        AppointmentRepository appointmentRepository,
                        InvoiceRepository invoiceRepository,
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminStatsResponseDto getAdminStats() {
        long totalPatients = patientRepository.count();
        long totalDoctors = doctorRepository.count();
        long totalAppointments = appointmentRepository.count();

        long completedAppointments = appointmentRepository.findAll().stream()
                .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()))
                .count();

        List<Invoice> paidInvoices = invoiceRepository.findAll().stream()
                .filter(i -> "PAID".equalsIgnoreCase(i.getPaymentStatus()))
                .toList();

        BigDecimal totalRevenue = paidInvoices.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminStatsResponseDto(totalPatients, totalDoctors, totalAppointments, completedAppointments, totalRevenue);
    }

    public DoctorResponseDto registerDoctor(RegisterDoctorRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        Role doctorRole = roleRepository.findByName(RoleName.ROLE_DOCTOR)
                .orElseThrow(() -> new RuntimeException("ROLE_DOCTOR not found"));
        user.getRoles().add(doctorRole);

        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setDepartment(dto.getDepartment());
        doctor.setConsultationFee(dto.getConsultationFee());

        Doctor savedDoctor = doctorRepository.save(doctor);

        return new DoctorResponseDto(
                savedDoctor.getId(),
                savedDoctor.getFirstName(),
                savedDoctor.getLastName(),
                savedDoctor.getSpecialization(),
                savedDoctor.getDepartment(),
                savedDoctor.getConsultationFee(),
                savedUser.getEmail()
        );
    }
}
