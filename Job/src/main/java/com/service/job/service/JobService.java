package com.service.job.service;

import com.service.job.client.ApplicationServiceClient;
import com.service.job.dto.ApplicationDto;
import com.service.job.dto.JobCreateDto;
import com.service.job.dto.JobResponseDto;
import com.service.job.model.Job;
import com.service.job.model.Job.JobStatus;
import com.service.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {
    
    private final JobRepository jobRepository;
    private final ApplicationServiceClient applicationServiceClient;
    
    public JobResponseDto createJob(JobCreateDto dto, Long employerId) {
        // Check for existing job with all fields matching
        List<Job> existingJobs = jobRepository.findExactJobMatch(
                dto.getTitle(),
                dto.getLocation(),
                dto.getCompany(),
                dto.getSalaryMin(),
                dto.getSalaryMax(),
                dto.getStatus()
        );
        if (!existingJobs.isEmpty()) {
            throw new RuntimeException("A job with the same details already exists for this company and is open.");
        }
        Job job = Job.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .salaryMin(dto.getSalaryMin())
                .salaryMax(dto.getSalaryMax())
                .company(dto.getCompany())
                .status(dto.getStatus())
                .employerId(employerId)
                .build();
        
        Job savedJob = jobRepository.save(job);
        return mapToResponseDto(savedJob);
    }
    
    public JobResponseDto updateJob(Long jobId, JobCreateDto dto, Long employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized to update this job");
        }
        
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setCompany(dto.getCompany());
        job.setStatus(dto.getStatus());
        
        Job updatedJob = jobRepository.save(job);
        return mapToResponseDto(updatedJob);
    }
    
    public void deleteJob(Long jobId, Long employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized to delete this job");
        }
        
        jobRepository.delete(job);
    }
    
    public List<JobResponseDto> getAllJobs(String title, String location, String companyName, 
                                          Double minSalary, Double maxSalary) {
        List<Job> jobs = jobRepository.findJobsWithFilters(title, location, companyName, 
                                                           minSalary, maxSalary, JobStatus.OPEN);
        
        return jobs.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public JobResponseDto getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        return mapToResponseDto(job);
    }
    
    public List<JobResponseDto> getJobsByEmployer(Long employerId) {
        List<Job> jobs = jobRepository.findByEmployerId(employerId);
        
        return jobs.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<ApplicationDto> getApplicationsForJob(Long jobId, Long employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Only check ownership if employerId is provided (authenticated request)
        if (employerId != null && !job.getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized to view applications for this job");
        }
        
        return applicationServiceClient.getApplicationsForJob(jobId);
    }
    
    private JobResponseDto mapToResponseDto(Job job) {
        return JobResponseDto.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .postedDate(job.getPostedDate())
                .status(job.getStatus())
                .employerId(job.getEmployerId())
                .company(job.getCompany())
                .build();
    }
}
