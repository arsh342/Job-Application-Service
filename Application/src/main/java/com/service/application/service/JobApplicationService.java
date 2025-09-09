package com.service.application.service;

import com.service.application.client.AuthServiceClient;
import com.service.application.client.JobServiceClient;
import com.service.application.client.UserDto;
import com.service.application.dto.*;
import com.service.application.model.JobApplication;
import com.service.application.model.JobApplication.ApplicationStatus;
import com.service.application.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationService {
    
    private final JobApplicationRepository jobApplicationRepository;
    private final JobServiceClient jobServiceClient;
    private final AuthServiceClient authServiceClient;
    
    public ApplicationResponseDto applyToJob(ApplicationCreateDto dto, Long applicantId) {
        // Check if already applied
        if (jobApplicationRepository.existsByJobIdAndApplicantId(dto.getJobId(), applicantId)) {
            throw new RuntimeException("You have already applied to this job");
        }
        
        // Verify job exists by calling job service
        JobDto job;
        try {
            job = jobServiceClient.getJobById(dto.getJobId());
        } catch (Exception e) {
            throw new RuntimeException("Job not found or not available");
        }
        
        if (!"OPEN".equals(job.getStatus())) {
            throw new RuntimeException("Job is not open for applications");
        }
        
        JobApplication application = JobApplication.builder()
                .jobId(dto.getJobId())
                .applicantId(applicantId)
                .coverLetter(dto.getCoverLetter())
                .resumeUrl(dto.getResumeUrl())
                .status(ApplicationStatus.APPLIED)
                .build();
        
        JobApplication savedApplication = jobApplicationRepository.save(application);
        return mapToResponseDto(savedApplication, job);
    }
    
    public ApplicationResponseDto updateApplication(Long applicationId, ApplicationCreateDto dto, Long applicantId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!application.getApplicantId().equals(applicantId)) {
            throw new RuntimeException("Unauthorized to update this application");
        }
        
        if (application.getStatus() != ApplicationStatus.APPLIED) {
            throw new RuntimeException("Cannot update application that has been processed");
        }
        
        // Verify new job exists
        JobDto job;
        try {
            job = jobServiceClient.getJobById(dto.getJobId());
        } catch (Exception e) {
            throw new RuntimeException("Job not found or not available");
        }
        
        if (!"OPEN".equals(job.getStatus())) {
            throw new RuntimeException("Job is not open for applications");
        }
        
        application.setJobId(dto.getJobId());
        application.setCoverLetter(dto.getCoverLetter());
        application.setResumeUrl(dto.getResumeUrl());
        JobApplication updatedApplication = jobApplicationRepository.save(application);
        
        return mapToResponseDto(updatedApplication, job);
    }
    
    public void withdrawApplication(Long applicationId, Long applicantId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!application.getApplicantId().equals(applicantId)) {
            throw new RuntimeException("Unauthorized to withdraw this application");
        }
        
        jobApplicationRepository.delete(application);
    }
    
    public ApplicationResponseDto getApplicationById(Long applicationId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        JobDto job = jobServiceClient.getJobById(application.getJobId());
        
        return mapToResponseDto(application, job);
    }
    
    public List<ApplicationResponseDto> getApplicationsForJob(Long jobId) {
        List<JobApplication> applications = jobApplicationRepository.findByJobId(jobId);
        
        return applications.stream()
                .map(application -> {
                    try {
                        JobDto job = jobServiceClient.getJobById(application.getJobId());
                        return mapToResponseDto(application, job);
                    } catch (Exception e) {
                        // Job might be deleted or not accessible
                        JobDto placeholderJob = new JobDto();
                        placeholderJob.setJobId(application.getJobId());
                        placeholderJob.setTitle("Job Unavailable (ID: " + application.getJobId() + ")");
                        placeholderJob.setCompany("Unknown Company");
                        placeholderJob.setLocation("Location Not Available");
                        placeholderJob.setDescription("This job is no longer available or has been removed.");
                        placeholderJob.setStatus("CLOSED");
                        
                        return mapToResponseDto(application, placeholderJob);
                    }
                })
                .collect(Collectors.toList());
    }
    
    public List<ApplicationResponseDto> getAllApplications() {
        List<JobApplication> applications = jobApplicationRepository.findAll();
        
        return applications.stream()
                .map(application -> {
                    try {
                        JobDto job = jobServiceClient.getJobById(application.getJobId());
                        return mapToResponseDto(application, job);
                    } catch (Exception e) {
                        // If job service fails, create a minimal response
                        JobDto errorJob = JobDto.builder()
                                .jobId(application.getJobId())
                                .title("Error loading job details")
                                .description("Job service unavailable")
                                .build();
                        return mapToResponseDto(application, errorJob);
                    }
                })
                .collect(Collectors.toList());
    }
    
    public List<ApplicationResponseDto> getApplicationsByApplicant(Long applicantId) {
        List<JobApplication> applications = jobApplicationRepository.findByApplicantId(applicantId);
        
        return applications.stream()
                .map(application -> {
                    try {
                        JobDto job = jobServiceClient.getJobById(application.getJobId());
                        return mapToResponseDto(application, job);
                    } catch (Exception e) {
                        // Job might be deleted or not accessible
                        // Create a placeholder job for the application
                        JobDto placeholderJob = new JobDto();
                        placeholderJob.setJobId(application.getJobId());
                        placeholderJob.setTitle("Job Unavailable (ID: " + application.getJobId() + ")");
                        placeholderJob.setCompany("Unknown Company");
                        placeholderJob.setLocation("Location Not Available");
                        placeholderJob.setDescription("This job is no longer available or has been removed.");
                        placeholderJob.setStatus("CLOSED");
                        
                        return mapToResponseDto(application, placeholderJob);
                    }
                })
                .collect(Collectors.toList());
    }
    
    public ApplicationResponseDto updateApplicationStatus(Long applicationId, ApplicationStatusUpdateDto dto) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        application.setStatus(dto.getStatus());
        JobApplication updatedApplication = jobApplicationRepository.save(application);
        
        try {
            JobDto job = jobServiceClient.getJobById(application.getJobId());
            return mapToResponseDto(updatedApplication, job);
        } catch (Exception e) {
            // Job might be deleted or not accessible
            JobDto placeholderJob = new JobDto();
            placeholderJob.setJobId(application.getJobId());
            placeholderJob.setTitle("Job Unavailable (ID: " + application.getJobId() + ")");
            placeholderJob.setCompany("Unknown Company");
            placeholderJob.setLocation("Location Not Available");
            placeholderJob.setDescription("This job is no longer available or has been removed.");
            placeholderJob.setStatus("CLOSED");
            
            return mapToResponseDto(updatedApplication, placeholderJob);
        }
    }
    
    private ApplicationResponseDto mapToResponseDto(JobApplication application, JobDto job) {
        String applicantName = "Unknown";
        String applicantEmail = "unknown@example.com";
        
        try {
            UserDto user = authServiceClient.getUserById(application.getApplicantId());
            applicantName = user.getName();
            applicantEmail = user.getEmail();
        } catch (Exception e) {
            // If we can't fetch user details, use defaults
            applicantName = "Applicant #" + application.getApplicantId();
            applicantEmail = "applicant" + application.getApplicantId() + "@unknown.com";
        }
        
        return ApplicationResponseDto.builder()
                .applicationId(application.getApplicationId())
                .jobId(application.getJobId())
                .applicantId(application.getApplicantId())
                .applicantName(applicantName)
                .applicantEmail(applicantEmail)
                .status(application.getStatus())
                .appliedDate(application.getAppliedDate())
                .coverLetter(application.getCoverLetter())
                .resumeUrl(application.getResumeUrl())
                .job(job)
                .build();
    }
}
