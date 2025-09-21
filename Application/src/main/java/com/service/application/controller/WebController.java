package com.service.application.controller;

import com.service.application.client.JobServiceClient;
import com.service.application.dto.JobDto;
import com.service.application.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class WebController {
    @Autowired
    private JobServiceClient jobServiceClient;
    @Autowired
    private GeminiService geminiService;
    
    // Helper method to add common user and token attributes to model
    private void addCommonAttributes(HttpServletRequest request, Model model) {
        // Add token if present
        String token = request.getParameter("token");
        String authHeader = request.getHeader("Authorization");
        
        if (token != null) {
            model.addAttribute("token", token);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            model.addAttribute("token", authHeader.substring(7));
        }

        // Add user information from authentication filter
        String userName = (String) request.getAttribute("userName");
        String userEmail = (String) request.getAttribute("userEmail");
        String userType = (String) request.getAttribute("userType");
        Long userId = (Long) request.getAttribute("userId");
        
        if (userName != null) {
            model.addAttribute("userName", userName);
        }
        if (userEmail != null) {
            model.addAttribute("userEmail", userEmail);
        }
        if (userType != null) {
            model.addAttribute("userType", userType);
        }
        if (userId != null) {
            model.addAttribute("userId", userId);
        }
    }
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        addCommonAttributes(request, model);
        return "dashboard";
    }
    
    @GetMapping("/browse-jobs")
    public String browseJobs(HttpServletRequest request, Model model) {
        addCommonAttributes(request, model);
        return "browse-jobs";
    }
    
    @GetMapping("/my-applications")
    public String myApplications(HttpServletRequest request, Model model) {
        addCommonAttributes(request, model);
        return "my-applications";
    }
    
    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        addCommonAttributes(request, model);
        return "profile";
    }
    
    @GetMapping("/job-details")
    public String jobDetails(HttpServletRequest request, Model model) {
        addCommonAttributes(request, model);
        String jobIdParam = request.getParameter("id");
        if (jobIdParam != null) {
            try {
                Long jobId = Long.parseLong(jobIdParam);
                JobDto job = jobServiceClient.getJobById(jobId);
                model.addAttribute("job", job);
                // Do NOT generate summary here anymore
            } catch (Exception e) {
                // Optionally handle error
            }
        }
        return "job-details";
    }
    
    @GetMapping("/status-demo")
    public String statusDemo(HttpServletRequest request, Model model) {
        addCommonAttributes(request, model);
        return "status-demo";
    }
    
    @GetMapping("/api/gemini-summary")
    @ResponseBody
    public String getGeminiSummary(@RequestParam("jobId") Long jobId) {
        try {
            JobDto job = jobServiceClient.getJobById(jobId);
            return geminiService.summarizeJob(job.getDescription());
        } catch (Exception e) {
            return "Could not generate summary.";
        }
    }
}
