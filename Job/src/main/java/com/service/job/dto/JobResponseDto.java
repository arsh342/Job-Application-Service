package com.service.job.dto;

import com.service.job.model.Job.JobStatus;
import com.service.job.model.Job.JobType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponseDto {
    
    private Long jobId;
    private String title;
    private String description;
    private String location;
    private String company;
    private Double salaryMin;
    private Double salaryMax;
    private LocalDate postedDate;
    private JobType jobType;
    private JobStatus status;
    private Long employerId;
}
