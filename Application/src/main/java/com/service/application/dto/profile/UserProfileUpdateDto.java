package com.service.application.dto.profile;

import com.service.application.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateDto {
    
    private String name;
    private String phone;
    private String summary;
    private String location;
    private String website;
    private String linkedin;
    private String github;
    private String currentPosition;
    private String currentCompany;
    private Integer yearsOfExperience;
    private Double expectedSalary;
    private String preferredLocation;
    private UserProfile.JobType jobTypePreference;
    private UserProfile.WorkMode workModePreference;
    
    private List<SkillUpdateDto> skills;
    private List<ExperienceUpdateDto> experiences;
    private List<EducationUpdateDto> educations;
    private List<ProjectUpdateDto> projects;
    private List<CertificationUpdateDto> certifications;
    private List<LanguageUpdateDto> languages;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillUpdateDto {
        private Long id; // null for new skills
        private String name;
        private Skill.SkillLevel level;
        private Skill.SkillCategory category;
        private Integer yearsOfExperience;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExperienceUpdateDto {
        private Long id; // null for new experiences
        private String position;
        private String company;
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isCurrent;
        private String description;
        private Experience.EmploymentType employmentType;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EducationUpdateDto {
        private Long id; // null for new educations
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isCurrent;
        private Double grade;
        private String gradeScale;
        private String description;
        private Education.EducationLevel educationLevel;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectUpdateDto {
        private Long id; // null for new projects
        private String name;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private String projectUrl;
        private String repositoryUrl;
        private String technologiesUsed;
        private Boolean isFeatured;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CertificationUpdateDto {
        private Long id; // null for new certifications
        private String name;
        private String issuingOrganization;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private String credentialId;
        private String credentialUrl;
        private String description;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LanguageUpdateDto {
        private Long id; // null for new languages
        private String name;
        private Language.Proficiency proficiency;
        private Boolean isNative;
    }
}
