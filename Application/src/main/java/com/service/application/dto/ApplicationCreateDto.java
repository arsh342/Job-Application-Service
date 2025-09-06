package com.service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCreateDto {
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    private String coverLetter;
    
    private String resumeUrl;
}
