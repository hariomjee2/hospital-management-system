package com.hms.backend.modules.pharmacy.repositories;

import com.hms.backend.modules.pharmacy.entities.PharmacyMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PharmacyMedicineRepository extends JpaRepository<PharmacyMedicine, UUID> {
    List<PharmacyMedicine> findByCategory(String category);
    List<PharmacyMedicine> findByMedicineNameContainingIgnoreCase(String name);

    @Query("SELECT m FROM PharmacyMedicine m WHERE m.stockQuantity <= m.reorderLevel")
    List<PharmacyMedicine> findLowStockMedicines();

    List<PharmacyMedicine> findByExpiryDateBefore(LocalDate thresholdDate);
}
