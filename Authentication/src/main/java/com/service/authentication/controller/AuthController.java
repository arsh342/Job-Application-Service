package com.service.authentication.controller;

import com.service.authentication.dto.*;
import com.service.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<UserValidationResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.ok(new UserValidationResponse(false, null, null, null, null, null, null));
            }
            
            String token = authHeader.substring(7);
            UserValidationResponse response = authService.validateToken(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new UserValidationResponse(false, null, null, null, null, null, null));
        }
    }
    
    @PostMapping("/validate-token")
    public ResponseEntity<UserValidationResponse> validateTokenFromBody(@RequestBody TokenValidationRequest request) {
        try {
            UserValidationResponse response = authService.validateToken(request.getToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new UserValidationResponse(false, null, null, null, null, null, null));
        }
    }
    
    @PutMapping("/users/{userId}/external-id")
    public ResponseEntity<?> updateExternalUserId(@PathVariable Long userId, @RequestParam Long externalUserId) {
        try {
            authService.updateExternalUserId(userId, externalUserId);
            return ResponseEntity.ok("External user ID updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication Service is running");
    }
}
