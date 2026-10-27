package com.hms.backend.modules.ipd.repositories;

import com.hms.backend.modules.ipd.entities.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WardRepository extends JpaRepository<Ward, UUID> {
}
