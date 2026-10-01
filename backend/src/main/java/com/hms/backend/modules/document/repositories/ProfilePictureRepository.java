package com.hms.backend.modules.document.repositories;

import com.hms.backend.modules.document.entities.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, UUID> {
    Optional<ProfilePicture> findByUserId(UUID userId);
}
