package com.service.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "educations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String institution;
    
    @Column(nullable = false)
    private String degree;
    
    @Column(name = "field_of_study")
    private String fieldOfStudy;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate; // null if currently studying
    
    @Column(name = "is_current")
    private Boolean isCurrent;
    
    private Double grade; // GPA or percentage
    
    @Column(name = "grade_scale")
    private String gradeScale; // "4.0", "10.0", "100%", etc.
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "education_level")
    private EducationLevel educationLevel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;
    
    public enum EducationLevel {
        HIGH_SCHOOL, DIPLOMA, BACHELORS, MASTERS, PhD, CERTIFICATION
    }
}
