package com.service.application.dto;

import com.service.application.model.JobApplication.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusUpdateDto {
    
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
}
