package com.hms.backend.modules.auth.controllers;

import com.hms.backend.modules.auth.dtos.AuthResponse;
import com.hms.backend.modules.auth.dtos.LoginRequest;
import com.hms.backend.modules.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Login authentication failed for " + loginRequest.getEmail() + ": " + e.getMessage());
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        }
    }
}
