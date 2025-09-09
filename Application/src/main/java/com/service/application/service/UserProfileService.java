package com.service.application.service;

import com.service.application.dto.profile.UserProfileDto;
import com.service.application.dto.profile.UserProfileUpdateDto;
import com.service.application.entity.*;
import com.service.application.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    
    public UserProfileDto getProfileByUserId(Long userId) {
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserIdWithDetails(userId);
        if (profileOpt.isPresent()) {
            UserProfile profile = profileOpt.get();
            // Force initialization of lazy collections within transaction
            profile.getSkills().size();
            profile.getExperiences().size();
            profile.getEducations().size();
            profile.getProjects().size();
            profile.getCertifications().size();
            profile.getLanguages().size();
            return convertToDto(profile);
        }
        
        // If profile doesn't exist, return a basic profile structure
        return UserProfileDto.builder()
                .userId(userId)
                .build();
    }
    
    public UserProfileDto createOrUpdateProfile(Long userId, String email, UserProfileUpdateDto updateDto) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(UserProfile.builder()
                        .userId(userId)
                        .email(email)
                        .build());
        
        // Update basic information
        if (updateDto.getName() != null) profile.setName(updateDto.getName());
        if (updateDto.getPhone() != null) profile.setPhone(updateDto.getPhone());
        if (updateDto.getSummary() != null) profile.setSummary(updateDto.getSummary());
        if (updateDto.getLocation() != null) profile.setLocation(updateDto.getLocation());
        if (updateDto.getWebsite() != null) profile.setWebsite(updateDto.getWebsite());
        if (updateDto.getLinkedin() != null) profile.setLinkedin(updateDto.getLinkedin());
        if (updateDto.getGithub() != null) profile.setGithub(updateDto.getGithub());
        if (updateDto.getCurrentPosition() != null) profile.setCurrentPosition(updateDto.getCurrentPosition());
        if (updateDto.getCurrentCompany() != null) profile.setCurrentCompany(updateDto.getCurrentCompany());
        if (updateDto.getYearsOfExperience() != null) profile.setYearsOfExperience(updateDto.getYearsOfExperience());
        if (updateDto.getExpectedSalary() != null) profile.setExpectedSalary(updateDto.getExpectedSalary());
        if (updateDto.getPreferredLocation() != null) profile.setPreferredLocation(updateDto.getPreferredLocation());
        if (updateDto.getJobTypePreference() != null) profile.setJobTypePreference(updateDto.getJobTypePreference());
        if (updateDto.getWorkModePreference() != null) profile.setWorkModePreference(updateDto.getWorkModePreference());
        
        // Update skills
        if (updateDto.getSkills() != null) {
            updateSkills(profile, updateDto.getSkills());
        }
        
        // Update experiences
        if (updateDto.getExperiences() != null) {
            updateExperiences(profile, updateDto.getExperiences());
        }
        
        // Update educations
        if (updateDto.getEducations() != null) {
            updateEducations(profile, updateDto.getEducations());
        }
        
        // Update projects
        if (updateDto.getProjects() != null) {
            updateProjects(profile, updateDto.getProjects());
        }
        
        // Update certifications
        if (updateDto.getCertifications() != null) {
            updateCertifications(profile, updateDto.getCertifications());
        }
        
        // Update languages
        if (updateDto.getLanguages() != null) {
            updateLanguages(profile, updateDto.getLanguages());
        }
        
        UserProfile savedProfile = userProfileRepository.save(profile);
        return convertToDto(savedProfile);
    }
    
    private void updateSkills(UserProfile profile, List<UserProfileUpdateDto.SkillUpdateDto> skillDtos) {
        // Clear existing skills
        profile.getSkills().clear();
        
        // Add new/updated skills
        for (UserProfileUpdateDto.SkillUpdateDto skillDto : skillDtos) {
            Skill skill = Skill.builder()
                    .name(skillDto.getName())
                    .level(skillDto.getLevel())
                    .category(skillDto.getCategory())
                    .yearsOfExperience(skillDto.getYearsOfExperience())
                    .userProfile(profile)
                    .build();
            profile.addSkill(skill);
        }
    }
    
    private void updateExperiences(UserProfile profile, List<UserProfileUpdateDto.ExperienceUpdateDto> experienceDtos) {
        profile.getExperiences().clear();
        
        for (UserProfileUpdateDto.ExperienceUpdateDto expDto : experienceDtos) {
            Experience experience = Experience.builder()
                    .position(expDto.getPosition())
                    .company(expDto.getCompany())
                    .location(expDto.getLocation())
                    .startDate(expDto.getStartDate())
                    .endDate(expDto.getEndDate())
                    .isCurrent(expDto.getIsCurrent())
                    .description(expDto.getDescription())
                    .employmentType(expDto.getEmploymentType())
                    .userProfile(profile)
                    .build();
            profile.addExperience(experience);
        }
    }
    
    private void updateEducations(UserProfile profile, List<UserProfileUpdateDto.EducationUpdateDto> educationDtos) {
        profile.getEducations().clear();
        
        for (UserProfileUpdateDto.EducationUpdateDto eduDto : educationDtos) {
            Education education = Education.builder()
                    .institution(eduDto.getInstitution())
                    .degree(eduDto.getDegree())
                    .fieldOfStudy(eduDto.getFieldOfStudy())
                    .startDate(eduDto.getStartDate())
                    .endDate(eduDto.getEndDate())
                    .isCurrent(eduDto.getIsCurrent())
                    .grade(eduDto.getGrade())
                    .gradeScale(eduDto.getGradeScale())
                    .description(eduDto.getDescription())
                    .educationLevel(eduDto.getEducationLevel())
                    .userProfile(profile)
                    .build();
            profile.addEducation(education);
        }
    }
    
    private void updateProjects(UserProfile profile, List<UserProfileUpdateDto.ProjectUpdateDto> projectDtos) {
        profile.getProjects().clear();
        
        for (UserProfileUpdateDto.ProjectUpdateDto projDto : projectDtos) {
            Project project = Project.builder()
                    .name(projDto.getName())
                    .description(projDto.getDescription())
                    .startDate(projDto.getStartDate())
                    .endDate(projDto.getEndDate())
                    .projectUrl(projDto.getProjectUrl())
                    .repositoryUrl(projDto.getRepositoryUrl())
                    .technologiesUsed(projDto.getTechnologiesUsed())
                    .isFeatured(projDto.getIsFeatured())
                    .userProfile(profile)
                    .build();
            profile.addProject(project);
        }
    }
    
    private void updateCertifications(UserProfile profile, List<UserProfileUpdateDto.CertificationUpdateDto> certificationDtos) {
        profile.getCertifications().clear();
        
        for (UserProfileUpdateDto.CertificationUpdateDto certDto : certificationDtos) {
            Certification certification = Certification.builder()
                    .name(certDto.getName())
                    .issuingOrganization(certDto.getIssuingOrganization())
                    .issueDate(certDto.getIssueDate())
                    .expiryDate(certDto.getExpiryDate())
                    .credentialId(certDto.getCredentialId())
                    .credentialUrl(certDto.getCredentialUrl())
                    .description(certDto.getDescription())
                    .userProfile(profile)
                    .build();
            profile.addCertification(certification);
        }
    }
    
    private void updateLanguages(UserProfile profile, List<UserProfileUpdateDto.LanguageUpdateDto> languageDtos) {
        profile.getLanguages().clear();
        
        for (UserProfileUpdateDto.LanguageUpdateDto langDto : languageDtos) {
            Language language = Language.builder()
                    .name(langDto.getName())
                    .proficiency(langDto.getProficiency())
                    .isNative(langDto.getIsNative())
                    .userProfile(profile)
                    .build();
            profile.addLanguage(language);
        }
    }
    
    private UserProfileDto convertToDto(UserProfile profile) {
        return UserProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .email(profile.getEmail())
                .name(profile.getName())
                .phone(profile.getPhone())
                .summary(profile.getSummary())
                .location(profile.getLocation())
                .website(profile.getWebsite())
                .linkedin(profile.getLinkedin())
                .github(profile.getGithub())
                .currentPosition(profile.getCurrentPosition())
                .currentCompany(profile.getCurrentCompany())
                .yearsOfExperience(profile.getYearsOfExperience())
                .expectedSalary(profile.getExpectedSalary())
                .preferredLocation(profile.getPreferredLocation())
                .jobTypePreference(profile.getJobTypePreference())
                .workModePreference(profile.getWorkModePreference())
                .skills(convertSkillsToDto(profile.getSkills()))
                .experiences(convertExperiencesToDto(profile.getExperiences()))
                .educations(convertEducationsToDto(profile.getEducations()))
                .projects(convertProjectsToDto(profile.getProjects()))
                .certifications(convertCertificationsToDto(profile.getCertifications()))
                .languages(convertLanguagesToDto(profile.getLanguages()))
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
    
    private List<UserProfileDto.SkillDto> convertSkillsToDto(List<Skill> skills) {
        if (skills == null) return new ArrayList<>();
        return skills.stream()
                .map(skill -> UserProfileDto.SkillDto.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .level(skill.getLevel())
                        .category(skill.getCategory())
                        .yearsOfExperience(skill.getYearsOfExperience())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<UserProfileDto.ExperienceDto> convertExperiencesToDto(List<Experience> experiences) {
        if (experiences == null) return new ArrayList<>();
        return experiences.stream()
                .map(exp -> UserProfileDto.ExperienceDto.builder()
                        .id(exp.getId())
                        .position(exp.getPosition())
                        .company(exp.getCompany())
                        .location(exp.getLocation())
                        .startDate(exp.getStartDate())
                        .endDate(exp.getEndDate())
                        .isCurrent(exp.getIsCurrent())
                        .description(exp.getDescription())
                        .employmentType(exp.getEmploymentType())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<UserProfileDto.EducationDto> convertEducationsToDto(List<Education> educations) {
        if (educations == null) return new ArrayList<>();
        return educations.stream()
                .map(edu -> UserProfileDto.EducationDto.builder()
                        .id(edu.getId())
                        .institution(edu.getInstitution())
                        .degree(edu.getDegree())
                        .fieldOfStudy(edu.getFieldOfStudy())
                        .startDate(edu.getStartDate())
                        .endDate(edu.getEndDate())
                        .isCurrent(edu.getIsCurrent())
                        .grade(edu.getGrade())
                        .gradeScale(edu.getGradeScale())
                        .description(edu.getDescription())
                        .educationLevel(edu.getEducationLevel())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<UserProfileDto.ProjectDto> convertProjectsToDto(List<Project> projects) {
        if (projects == null) return new ArrayList<>();
        return projects.stream()
                .map(proj -> UserProfileDto.ProjectDto.builder()
                        .id(proj.getId())
                        .name(proj.getName())
                        .description(proj.getDescription())
                        .startDate(proj.getStartDate())
                        .endDate(proj.getEndDate())
                        .projectUrl(proj.getProjectUrl())
                        .repositoryUrl(proj.getRepositoryUrl())
                        .technologiesUsed(proj.getTechnologiesUsed())
                        .isFeatured(proj.getIsFeatured())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<UserProfileDto.CertificationDto> convertCertificationsToDto(List<Certification> certifications) {
        if (certifications == null) return new ArrayList<>();
        return certifications.stream()
                .map(cert -> UserProfileDto.CertificationDto.builder()
                        .id(cert.getId())
                        .name(cert.getName())
                        .issuingOrganization(cert.getIssuingOrganization())
                        .issueDate(cert.getIssueDate())
                        .expiryDate(cert.getExpiryDate())
                        .credentialId(cert.getCredentialId())
                        .credentialUrl(cert.getCredentialUrl())
                        .description(cert.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<UserProfileDto.LanguageDto> convertLanguagesToDto(List<Language> languages) {
        if (languages == null) return new ArrayList<>();
        return languages.stream()
                .map(lang -> UserProfileDto.LanguageDto.builder()
                        .id(lang.getId())
                        .name(lang.getName())
                        .proficiency(lang.getProficiency())
                        .isNative(lang.getIsNative())
                        .build())
                .collect(Collectors.toList());
    }
}
