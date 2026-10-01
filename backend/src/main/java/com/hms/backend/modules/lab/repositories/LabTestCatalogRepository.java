package com.hms.backend.modules.lab.repositories;

import com.hms.backend.modules.lab.entities.LabTestCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabTestCatalogRepository extends JpaRepository<LabTestCatalog, UUID> {
    List<LabTestCatalog> findByDiseaseCategoryIgnoreCase(String diseaseCategory);
}
