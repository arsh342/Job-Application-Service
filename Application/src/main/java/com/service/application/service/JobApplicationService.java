package com.service.application.service;

import com.service.application.client.JobServiceClient;
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
                    JobDto job = jobServiceClient.getJobById(application.getJobId());
                    return mapToResponseDto(application, job);
                })
                .collect(Collectors.toList());
    }
    
    public List<ApplicationResponseDto> getApplicationsByApplicant(Long applicantId) {
        List<JobApplication> applications = jobApplicationRepository.findByApplicantId(applicantId);
        
        return applications.stream()
                .map(application -> {
                    JobDto job = jobServiceClient.getJobById(application.getJobId());
                    return mapToResponseDto(application, job);
                })
                .collect(Collectors.toList());
    }
    
    public ApplicationResponseDto updateApplicationStatus(Long applicationId, ApplicationStatusUpdateDto dto) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        application.setStatus(dto.getStatus());
        JobApplication updatedApplication = jobApplicationRepository.save(application);
        
        JobDto job = jobServiceClient.getJobById(application.getJobId());
        
        return mapToResponseDto(updatedApplication, job);
    }
    
    private ApplicationResponseDto mapToResponseDto(JobApplication application, JobDto job) {
        return ApplicationResponseDto.builder()
                .applicationId(application.getApplicationId())
                .jobId(application.getJobId())
                .applicantId(application.getApplicantId())
                .status(application.getStatus())
                .appliedDate(application.getAppliedDate())
                .coverLetter(application.getCoverLetter())
                .resumeUrl(application.getResumeUrl())
                .job(job)
                .build();
    }
}
