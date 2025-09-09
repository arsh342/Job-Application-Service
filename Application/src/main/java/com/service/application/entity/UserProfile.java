package com.service.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId; // References the user ID from Authentication service
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    private String phone;
    
    @Column(length = 1000)
    private String summary; // Professional summary
    
    private String location;
    private String website;
    private String linkedin;
    private String github;
    
    // Employment details
    @Column(name = "current_position")
    private String currentPosition;
    
    @Column(name = "current_company")
    private String currentCompany;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "expected_salary")
    private Double expectedSalary;
    
    @Column(name = "preferred_location")
    private String preferredLocation;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type_preference")
    private JobType jobTypePreference;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode_preference")
    private WorkMode workModePreference;
    
    // One-to-many relationships
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();
    
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();
    
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Education> educations = new ArrayList<>();
    
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Project> projects = new ArrayList<>();
    
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Certification> certifications = new ArrayList<>();
    
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Language> languages = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum JobType {
        FULL_TIME, PART_TIME, CONTRACT, FREELANCE, INTERNSHIP
    }
    
    public enum WorkMode {
        REMOTE, ONSITE, HYBRID
    }
    
    // Helper methods to manage relationships
    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setUserProfile(this);
    }
    
    public void removeSkill(Skill skill) {
        skills.remove(skill);
        skill.setUserProfile(null);
    }
    
    public void addExperience(Experience experience) {
        experiences.add(experience);
        experience.setUserProfile(this);
    }
    
    public void removeExperience(Experience experience) {
        experiences.remove(experience);
        experience.setUserProfile(null);
    }
    
    public void addEducation(Education education) {
        educations.add(education);
        education.setUserProfile(this);
    }
    
    public void removeEducation(Education education) {
        educations.remove(education);
        education.setUserProfile(null);
    }
    
    public void addProject(Project project) {
        projects.add(project);
        project.setUserProfile(this);
    }
    
    public void removeProject(Project project) {
        projects.remove(project);
        project.setUserProfile(null);
    }
    
    public void addCertification(Certification certification) {
        certifications.add(certification);
        certification.setUserProfile(this);
    }
    
    public void removeCertification(Certification certification) {
        certifications.remove(certification);
        certification.setUserProfile(null);
    }
    
    public void addLanguage(Language language) {
        languages.add(language);
        language.setUserProfile(this);
    }
    
    public void removeLanguage(Language language) {
        languages.remove(language);
        language.setUserProfile(null);
    }
}
