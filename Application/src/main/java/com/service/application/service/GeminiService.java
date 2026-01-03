package com.service.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.service.application.dto.JobDto;
import com.service.application.dto.profile.UserProfileDto;

@Service
public class GeminiService {
    private final String apiKey;
    private final Client client;

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.client = Client.builder().apiKey(apiKey).build();
    }

    public String summarizeJob(String jobDescription) {
        String prompt = "Summarize this job in 2-3 sentences for a candidate: " + jobDescription;
        
        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );
            
            return response.text().trim();
        } catch (Exception e) {
            System.err.println("Error in summarizeJob: " + e.getMessage());
            e.printStackTrace();
            return "Unable to generate summary at this time.";
        }
    }

    public String generateComprehensiveSummary(JobDto job, UserProfileDto userProfile) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a career advisor helping a job candidate understand a job opportunity. ");
        prompt.append("Generate a comprehensive, personalized job summary that includes:\n\n");
        
        prompt.append("JOB INFORMATION:\n");
        prompt.append("Title: ").append(job.getTitle()).append("\n");
        prompt.append("Company: ").append(job.getCompany()).append("\n");
        prompt.append("Location: ").append(job.getLocation()).append("\n");
        if (job.getSalaryMin() != null || job.getSalaryMax() != null) {
            prompt.append("Salary: ");
            if (job.getSalaryMin() != null && job.getSalaryMax() != null) {
                prompt.append("₹").append(job.getSalaryMin()).append(" - ₹").append(job.getSalaryMax());
            } else if (job.getSalaryMin() != null) {
                prompt.append("₹").append(job.getSalaryMin()).append("+");
            } else {
                prompt.append("Up to ₹").append(job.getSalaryMax());
            }
            prompt.append(" per year\n");
        }
        prompt.append("Job Type: Full-time").append("\n");
        prompt.append("Description: ").append(job.getDescription()).append("\n");
        
        if (userProfile != null) {
            prompt.append("\nCANDIDATE PROFILE:\n");
            if (userProfile.getSummary() != null && !userProfile.getSummary().trim().isEmpty()) {
                prompt.append("Professional Summary: ").append(userProfile.getSummary()).append("\n");
            }
            
            if (userProfile.getSkills() != null && !userProfile.getSkills().isEmpty()) {
                prompt.append("Skills: ");
                userProfile.getSkills().forEach(skill -> 
                    prompt.append(skill.getName()).append(" (").append(skill.getLevel()).append("), ")
                );
                prompt.append("\n");
            }
            
            if (userProfile.getExperiences() != null && !userProfile.getExperiences().isEmpty()) {
                prompt.append("Experience: ");
                userProfile.getExperiences().forEach(exp -> 
                    prompt.append(exp.getPosition()).append(" at ").append(exp.getCompany())
                          .append(" (").append(exp.getStartDate()).append(" - ").append(exp.getEndDate() != null ? exp.getEndDate() : "Present").append("), ")
                );
                prompt.append("\n");
            }
            
            if (userProfile.getEducations() != null && !userProfile.getEducations().isEmpty()) {
                prompt.append("Education: ");
                userProfile.getEducations().forEach(edu -> 
                    prompt.append(edu.getDegree()).append(" in ").append(edu.getFieldOfStudy())
                          .append(" from ").append(edu.getInstitution()).append(", ")
                );
                prompt.append("\n");
            }
        }
        
        prompt.append("\nGenerate a job summary in EXACTLY this bullet point format (each bullet point on a new line):\n\n");
        prompt.append("• **Job Title** at **Company** - Brief 1-sentence overview\n");
        prompt.append("• **Key Responsibilities**:\n");
        prompt.append("  - **Responsibility 1**\n");
        prompt.append("  - **Responsibility 2**\n");
        prompt.append("  - **Responsibility 3**\n");
        prompt.append("• **Required Skills**: **Skill1**, **Skill2**, **Skill3**\n");
        
        if (userProfile != null && (userProfile.getSkills() != null && !userProfile.getSkills().isEmpty() || 
            userProfile.getSummary() != null && !userProfile.getSummary().trim().isEmpty())) {
            prompt.append("• **Profile Match: XX%** - Brief explanation of match\n");
        } else {
            prompt.append("• **Profile Match**: Complete your profile to see match percentage\n");
        }
        
        prompt.append("• **Application Tip**: One specific actionable tip\n\n");
        prompt.append("IMPORTANT FORMATTING RULES:\n");
        prompt.append("- Use ONLY bullet points (•) - NO paragraphs\n");
        prompt.append("- Use **bold** for job titles, company names, skills, and key terms\n");
        prompt.append("- Calculate profile match percentage based on:\n");
        prompt.append("  * Skills alignment (40% weight)\n");
        prompt.append("  * Experience relevance (30% weight)\n");
        prompt.append("  * Education fit (20% weight)\n");
        prompt.append("  * Career progression (10% weight)\n");
        prompt.append("- Keep each bullet point concise (1-2 lines max)\n");
        prompt.append("- Generate the complete summary immediately");

        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt.toString(),
                    null
            );
            
            String generatedText = response.text();
            
            // Return the generated text directly without post-processing
            // to preserve bold formatting and bullet points
            return generatedText.trim();
            
        } catch (Exception e) {
            System.err.println("Error in generateComprehensiveSummary: " + e.getMessage());
            e.printStackTrace();
            // Fallback to simple summary if comprehensive generation fails
            return summarizeJob(job.getDescription());
        }
    }
}
