package com.hms.backend.modules.document.controllers;

import com.hms.backend.modules.auth.security.UserDetailsImpl;
import com.hms.backend.modules.document.dto.DocumentDto;
import com.hms.backend.modules.document.entities.PatientDocument;
import com.hms.backend.modules.document.entities.ProfilePicture;
import com.hms.backend.modules.document.services.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/users/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProfilePicture pic = documentService.uploadProfilePicture(
                userDetails.getId(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );
        return ResponseEntity.ok().body("Profile picture uploaded successfully (" + pic.getFileSize() + " bytes).");
    }

    @PostMapping("/users/profile-picture/base64")
    public ResponseEntity<?> uploadProfilePictureBase64(@RequestBody Map<String, String> body) {
        UUID userId = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl userDetails) {
            userId = userDetails.getId();
        }

        if (userId == null && body.containsKey("userId") && body.get("userId") != null && !body.get("userId").isEmpty()) {
            userId = UUID.fromString(body.get("userId"));
        }

        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required.");
        }

        String base64Data = body.get("base64Data");
        String fileName = body.getOrDefault("fileName", "avatar.jpg");
        String contentType = body.getOrDefault("contentType", "image/jpeg");

        if (base64Data != null && base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1];
        }

        byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
        ProfilePicture pic = documentService.uploadProfilePicture(
                userId,
                fileName,
                contentType,
                bytes
        );
        return ResponseEntity.ok().body("Profile picture uploaded successfully (" + pic.getFileSize() + " bytes).");
    }

    @GetMapping("/users/profile-picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable UUID userId) {
        ProfilePicture pic = documentService.getProfilePicture(userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pic.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(pic.getContentType()))
                .body(pic.getData());
    }

    @PostMapping("/documents")
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "patientId", required = false) UUID patientId) throws IOException {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DocumentDto doc = documentService.uploadPatientDocument(
                userDetails.getId(),
                patientId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );
        return ResponseEntity.ok(doc);
    }

    @PostMapping("/documents/base64")
    public ResponseEntity<DocumentDto> uploadDocumentBase64(@RequestBody Map<String, String> body) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String base64Data = body.get("base64Data");
        String fileName = body.getOrDefault("fileName", "document.pdf");
        String contentType = body.getOrDefault("contentType", "application/pdf");
        String patientIdStr = body.get("patientId");
        UUID patientId = (patientIdStr != null && !patientIdStr.isEmpty()) ? UUID.fromString(patientIdStr) : null;

        if (base64Data != null && base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1];
        }

        byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
        DocumentDto doc = documentService.uploadPatientDocument(
                userDetails.getId(),
                patientId,
                fileName,
                contentType,
                bytes
        );
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/documents/patient/{patientId}")
    public ResponseEntity<List<DocumentDto>> getPatientDocuments(@PathVariable UUID patientId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isDoctorOrAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_DOCTOR") || role.equals("ROLE_ADMIN"));

        return ResponseEntity.ok(documentService.getPatientDocuments(userDetails.getId(), isDoctorOrAdmin, patientId));
    }

    @GetMapping("/documents/my")
    public ResponseEntity<List<DocumentDto>> getMyDocuments() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(documentService.getPatientDocuments(userDetails.getId(), false, null));
    }

    @GetMapping("/documents/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isDoctorOrAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_DOCTOR") || role.equals("ROLE_ADMIN"));

        PatientDocument doc = documentService.getDocumentForDownload(userDetails.getId(), isDoctorOrAdmin, id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getDocumentName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(doc.getData());
    }
}
