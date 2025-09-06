package com.service.job.dto;

import com.service.job.model.Job.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCreateDto {
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;
    
    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;
    
    @NotBlank(message = "Company name is required")
    private String company;
    
    @Positive(message = "Minimum salary must be positive")
    private Double salaryMin;
    
    @Positive(message = "Maximum salary must be positive")
    private Double salaryMax;
    
    private JobStatus status = JobStatus.OPEN;
}
