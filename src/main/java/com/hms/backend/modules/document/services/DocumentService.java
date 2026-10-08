package com.hms.backend.modules.document.services;

import com.hms.backend.modules.auth.entities.User;
import com.hms.backend.modules.auth.repositories.UserRepository;
import com.hms.backend.modules.document.dto.DocumentDto;
import com.hms.backend.modules.document.entities.PatientDocument;
import com.hms.backend.modules.document.entities.ProfilePicture;
import com.hms.backend.modules.document.repositories.PatientDocumentRepository;
import com.hms.backend.modules.document.repositories.ProfilePictureRepository;
import com.hms.backend.modules.patient.entities.Patient;
import com.hms.backend.modules.patient.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final ProfilePictureRepository profilePictureRepository;
    private final PatientDocumentRepository patientDocumentRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    private static final long MIN_FILE_SIZE = 1024; // 1 KB
    private static final long MAX_PROFILE_PIC_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10 MB

    public DocumentService(ProfilePictureRepository profilePictureRepository,
                           PatientDocumentRepository patientDocumentRepository,
                           UserRepository userRepository,
                           PatientRepository patientRepository) {
        this.profilePictureRepository = profilePictureRepository;
        this.patientDocumentRepository = patientDocumentRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    public ProfilePicture uploadProfilePicture(UUID userId, String fileName, String contentType, byte[] data) {
        if (data == null || data.length < MIN_FILE_SIZE) {
            throw new RuntimeException("File size is too small. Minimum required size is 1KB.");
        }
        if (data.length > MAX_PROFILE_PIC_SIZE) {
            throw new RuntimeException("Profile picture size exceeds maximum 5MB limit.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        ProfilePicture profilePic = profilePictureRepository.findByUserId(userId)
                .orElse(new ProfilePicture());

        // Compress and downscale profile picture to 256x256 max to optimize DB storage
        byte[] compressedData = compressAndResizeImage(data, 256, 256);

        profilePic.setUser(user);
        profilePic.setFileName(fileName);
        profilePic.setContentType("image/jpeg");
        profilePic.setData(compressedData);
        profilePic.setFileSize(compressedData.length);

        return profilePictureRepository.save(profilePic);
    }

    public ProfilePicture getProfilePicture(UUID userId) {
        return profilePictureRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile picture not found for user: " + userId));
    }

    public DocumentDto uploadPatientDocument(UUID currentUserId, UUID targetPatientId, String documentName, String contentType, byte[] data) {
        if (data == null || data.length < MIN_FILE_SIZE) {
            throw new RuntimeException("File size is too small. Minimum required size is 1KB.");
        }
        if (data.length > MAX_DOCUMENT_SIZE) {
            throw new RuntimeException("Document size exceeds maximum 10MB limit.");
        }

        User uploader = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUserId));

        Patient patient;
        if (targetPatientId != null) {
            patient = patientRepository.findById(targetPatientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found: " + targetPatientId));
        } else {
            patient = patientRepository.findByUserId(currentUserId)
                    .orElseThrow(() -> new RuntimeException("Patient profile not found for user: " + currentUserId));
        }

        PatientDocument doc = new PatientDocument();
        doc.setPatient(patient);
        doc.setUploadedBy(uploader);
        doc.setDocumentName(documentName);
        doc.setContentType(contentType);
        doc.setData(data);
        doc.setFileSize(data.length);

        PatientDocument saved = patientDocumentRepository.save(doc);
        return mapToDto(saved);
    }

    public List<DocumentDto> getPatientDocuments(UUID currentUserId, boolean isDoctorOrAdmin, UUID targetPatientId) {
        Patient patient;
        if (targetPatientId != null) {
            patient = patientRepository.findById(targetPatientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found: " + targetPatientId));
        } else {
            patient = patientRepository.findByUserId(currentUserId)
                    .orElseThrow(() -> new RuntimeException("Patient profile not found for user: " + currentUserId));
        }

        // Strict Access Control: One patient cannot see for another patient
        if (!isDoctorOrAdmin && !patient.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("Access Denied: You cannot view another patient's medical records.");
        }

        return patientDocumentRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public PatientDocument getDocumentForDownload(UUID currentUserId, boolean isDoctorOrAdmin, UUID documentId) {
        PatientDocument doc = patientDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

        // Strict Access Control Check
        if (!isDoctorOrAdmin && !doc.getPatient().getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("Access Denied: You cannot download another patient's medical records.");
        }

        return doc;
    }

    private byte[] compressAndResizeImage(byte[] originalData, int targetWidth, int targetHeight) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(originalData);
            BufferedImage originalImage = ImageIO.read(bais);
            if (originalImage == null) {
                return originalData;
            }

            int origWidth = originalImage.getWidth();
            int origHeight = originalImage.getHeight();

            if (origWidth > targetWidth || origHeight > targetHeight) {
                double ratio = Math.min((double) targetWidth / origWidth, (double) targetHeight / origHeight);
                int newWidth = (int) (origWidth * ratio);
                int newHeight = (int) (origHeight * ratio);

                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
                g2d.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", baos);
                byte[] compressedBytes = baos.toByteArray();

                System.out.println("Image compressed: Original " + originalData.length + " bytes -> Reduced " + compressedBytes.length + " bytes.");
                return compressedBytes;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("Compression warning: " + e.getMessage());
            return originalData;
        }
    }

    private DocumentDto mapToDto(PatientDocument doc) {
        String patientName = doc.getPatient().getFirstName() + " " + doc.getPatient().getLastName();
        return new DocumentDto(
                doc.getId(),
                doc.getPatient().getId(),
                patientName,
                doc.getDocumentName(),
                doc.getContentType(),
                doc.getFileSize(),
                doc.getCreatedAt()
        );
    }
}
