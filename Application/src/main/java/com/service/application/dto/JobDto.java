package com.service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDto {
    private Long jobId;
    private String title;
    private String description;
    private String location;
    private String company;
    private Double salaryMin;
    private Double salaryMax;
    private LocalDate postedDate;
    private String status;
    private Long employerId;
}
