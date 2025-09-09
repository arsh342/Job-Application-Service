package com.service.job.controller;

import com.service.job.dto.ApplicationDto;
import com.service.job.dto.JobCreateDto;
import com.service.job.dto.JobResponseDto;
import com.service.job.service.JobService;
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
public class JobController {
    
    private final JobService jobService;
    
    @PostMapping("/jobs")
    public ResponseEntity<?> createJob(@Valid @RequestBody JobCreateDto dto, HttpServletRequest request) {
        Long employerId = (Long) request.getAttribute("employerId");
        if (employerId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            JobResponseDto job = jobService.createJob(dto, employerId);
            return ResponseEntity.ok(job);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/jobs/{jobId}")
    public ResponseEntity<?> updateJob(@PathVariable Long jobId, 
                                      @Valid @RequestBody JobCreateDto dto, 
                                      HttpServletRequest request) {
        Long employerId = (Long) request.getAttribute("employerId");
        if (employerId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            JobResponseDto job = jobService.updateJob(jobId, dto, employerId);
            return ResponseEntity.ok(job);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId, HttpServletRequest request) {
        Long employerId = (Long) request.getAttribute("employerId");
        if (employerId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            jobService.deleteJob(jobId, employerId);
            return ResponseEntity.ok("Job deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponseDto>> getAllJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary) {
        
        List<JobResponseDto> jobs = jobService.getAllJobs(title, location, companyName, minSalary, maxSalary);
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/jobs/all")
    public ResponseEntity<List<JobResponseDto>> getAllJobsPublic() {
        List<JobResponseDto> jobs = jobService.getAllJobs(null, null, null, null, null);
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {
        try {
            JobResponseDto job = jobService.getJobById(jobId);
            return ResponseEntity.ok(job);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/jobs/my-jobs")
    public ResponseEntity<?> getMyJobs(HttpServletRequest request) {
        Long employerId = (Long) request.getAttribute("employerId");
        if (employerId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            List<JobResponseDto> jobs = jobService.getJobsByEmployer(employerId);
            return ResponseEntity.ok(jobs);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/employers/{employerId}/jobs")
    public ResponseEntity<?> getJobsByEmployer(@PathVariable Long employerId, HttpServletRequest request) {
        Long sessionEmployerId = (Long) request.getAttribute("employerId");
        if (sessionEmployerId == null || !sessionEmployerId.equals(employerId)) {
            return ResponseEntity.status(401).body("Authentication required");
        }

        try {
            List<JobResponseDto> jobs = jobService.getJobsByEmployer(employerId);
            return ResponseEntity.ok(jobs);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long jobId, HttpServletRequest request) {
        Long employerId = (Long) request.getAttribute("employerId");
        
        // For public access to applications (like job listings page), allow without authentication
        try {
            List<ApplicationDto> applications = jobService.getApplicationsForJob(jobId, employerId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long applicationId,
                                                    @RequestBody StatusUpdateRequest request,
                                                    HttpServletRequest httpRequest) {
        Long employerId = (Long) httpRequest.getAttribute("employerId");
        if (employerId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        try {
            // Delegate to Application service
            String applicationServiceUrl = "http://localhost:8082";
            
            // Create HTTP client request
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            
            // Create request body
            String requestBody = String.format("{\"status\":\"%s\"}", request.getStatus());
            
            java.net.http.HttpRequest httpReq = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(applicationServiceUrl + "/api/applications/" + applicationId + "/status"))
                    .header("Content-Type", "application/json")
                    .PUT(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            java.net.http.HttpResponse<String> response = client.send(httpReq, 
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return ResponseEntity.ok(response.body());
            } else {
                return ResponseEntity.status(response.statusCode()).body(response.body());
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating application status: " + e.getMessage());
        }
    }
    
    // Status update request class
    public static class StatusUpdateRequest {
        private String status;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
