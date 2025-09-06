package com.service.application.controller;

import com.service.application.dto.ApplicationCreateDto;
import com.service.application.dto.ApplicationResponseDto;
import com.service.application.dto.ApplicationStatusUpdateDto;
import com.service.application.model.JobApplication;
import com.service.application.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {
    
    private final JobApplicationService jobApplicationService;
    
    @PostMapping("/applications")
    public ResponseEntity<?> applyToJob(@Valid @RequestBody ApplicationCreateDto dto, HttpServletRequest request) {
        Long applicantId = (Long) request.getAttribute("applicantId");
        if (applicantId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            ApplicationResponseDto application = jobApplicationService.applyToJob(dto, applicantId);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<?> updateApplication(@PathVariable Long applicationId,
                                              @Valid @RequestBody ApplicationCreateDto dto,
                                              HttpServletRequest request) {
        Long applicantId = (Long) request.getAttribute("applicantId");
        if (applicantId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            ApplicationResponseDto application = jobApplicationService.updateApplication(applicationId, dto, applicantId);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<?> withdrawApplication(@PathVariable Long applicationId, HttpServletRequest request) {
        Long applicantId = (Long) request.getAttribute("applicantId");
        if (applicantId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            jobApplicationService.withdrawApplication(applicationId, applicantId);
            return ResponseEntity.ok("Application withdrawn successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long applicationId) {
        try {
            ApplicationResponseDto application = jobApplicationService.getApplicationById(applicationId);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long jobId) {
        try {
            List<ApplicationResponseDto> applications = jobApplicationService.getApplicationsForJob(jobId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/applications/apply")
    public ResponseEntity<?> applyToJobSimple(@Valid @RequestBody ApplicationCreateDto dto, HttpServletRequest request) {
        Long applicantId = (Long) request.getAttribute("applicantId");
        if (applicantId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            ApplicationResponseDto application = jobApplicationService.applyToJob(dto, applicantId);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/applications/my-applications")
    public ResponseEntity<?> getMyApplications(HttpServletRequest request) {
        Long applicantId = (Long) request.getAttribute("applicantId");
        if (applicantId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            List<ApplicationResponseDto> applications = jobApplicationService.getApplicationsByApplicant(applicantId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/applicants/{applicantId}/applications")
    public ResponseEntity<?> getApplicationsByApplicant(@PathVariable Long applicantId, HttpServletRequest request) {
        Long sessionApplicantId = (Long) request.getAttribute("applicantId");
        if (sessionApplicantId == null || !sessionApplicantId.equals(applicantId)) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            List<ApplicationResponseDto> applications = jobApplicationService.getApplicationsByApplicant(applicantId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long applicationId,
                                                     @RequestBody StatusUpdateRequest request) {
        try {
            ApplicationStatusUpdateDto dto = new ApplicationStatusUpdateDto();
            dto.setStatus(JobApplication.ApplicationStatus.valueOf(request.getStatus()));
            ApplicationResponseDto application = jobApplicationService.updateApplicationStatus(applicationId, dto);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Simple request class for status updates
    public static class StatusUpdateRequest {
        private String status;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
