package com.service.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;
    
    @Column(nullable = false)
    private Long jobId;
    
    @Column(nullable = false)
    private Long applicantId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;
    
    @Column
    private LocalDate appliedDate;
    
    @Column
    private String coverLetter;
    
    @Column
    private String resumeUrl;

    @PrePersist
    protected void onCreate() {
        appliedDate = LocalDate.now();
        if (status == null) {
            status = ApplicationStatus.APPLIED;
        }
    }
    
    public enum ApplicationStatus {
        APPLIED, SHORTLISTED, REJECTED, HIRED
    }
}
