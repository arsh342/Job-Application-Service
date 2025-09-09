package com.service.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Long applicationId;
    private Long jobId;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String status;
    private LocalDate appliedDate;
    private String coverLetter;
}
