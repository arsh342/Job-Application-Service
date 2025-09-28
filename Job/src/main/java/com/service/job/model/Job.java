package com.service.job.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String location;
    
    @Column
    private Double salary;
    
    @Column
    private LocalDate postedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;
    
    @Column(name = "employer_id", nullable = false)
    private Long employerId;
    
    @Column(nullable = false)
    private String company;
    
    @Column
    private Double salaryMin;
    
    @Column  
    private Double salaryMax;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobType jobType = JobType.FULL_TIME;

    @PrePersist
    protected void onCreate() {
        postedDate = LocalDate.now();
        if (status == null) {
            status = JobStatus.OPEN;
        }
        if (jobType == null) {
            jobType = JobType.FULL_TIME;
        }
    }
    
    public enum JobStatus {
        OPEN, CLOSED
    }
    
    public enum JobType {
        FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE, TEMPORARY
    }
}
