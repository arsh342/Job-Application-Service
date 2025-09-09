package com.service.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private SkillLevel level;
    
    @Enumerated(EnumType.STRING)
    private SkillCategory category;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;
    
    public enum SkillLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }
    
    public enum SkillCategory {
        PROGRAMMING_LANGUAGE, FRAMEWORK, DATABASE, TOOL, SOFT_SKILL, OTHER
    }
}
