package com.service.authentication.service;

import com.service.authentication.dto.*;
import com.service.authentication.entity.User;
import com.service.authentication.entity.UserType;
import com.service.authentication.repository.UserRepository;
import com.service.authentication.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Validate employer-specific fields
        if (request.getUserType() == UserType.EMPLOYER && 
            (request.getCompanyName() == null || request.getCompanyName().trim().isEmpty())) {
            throw new RuntimeException("Company name is required for employers");
        }
        
        // Create user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(request.getUserType())
                .phone(request.getPhone())
                .companyName(request.getCompanyName())
                .build();
        
        user = userRepository.save(user);
        
        // Return success response WITHOUT token (user needs to login separately)
        return AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .userType(user.getUserType())
                .externalUserId(user.getExternalUserId())
                .companyName(user.getCompanyName())
                // No token - user must login after registration
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate JWT token
            String token = jwtUtil.generateToken(
                    user.getEmail(), 
                    user.getId(), 
                    user.getUserType().name(),
                    user.getExternalUserId()
            );
            
            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .userType(user.getUserType())
                    .externalUserId(user.getExternalUserId())
                    .companyName(user.getCompanyName())
                    .build();
                    
        } catch (Exception e) {
            log.error("Authentication failed for email: {}", request.getEmail(), e);
            throw new RuntimeException("Invalid email or password");
        }
    }
    
    public UserValidationResponse validateToken(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            
            if (email != null && jwtUtil.validateToken(token, email)) {
                User user = userRepository.findByEmail(email)
                        .orElse(null);
                
                if (user != null) {
                    return new UserValidationResponse(
                            true,
                            user.getId(),
                            user.getEmail(),
                            user.getName(),
                            user.getUserType().name(),
                            user.getExternalUserId(),
                            user.getCompanyName()
                    );
                }
            }
            
            return new UserValidationResponse(false, null, null, null, null, null, null);
            
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return new UserValidationResponse(false, null, null, null, null, null, null);
        }
    }
    
    @Transactional
    public void updateExternalUserId(Long userId, Long externalUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setExternalUserId(externalUserId);
        userRepository.save(user);
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
