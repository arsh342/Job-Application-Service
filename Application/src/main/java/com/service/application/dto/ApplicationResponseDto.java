package com.service.application.dto;

import com.service.application.model.JobApplication.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDto {
    
    private Long applicationId;
    private Long jobId;
    private Long applicantId;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private String coverLetter;
    private String resumeUrl;
    private JobDto job;
}
