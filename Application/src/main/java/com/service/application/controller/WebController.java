package com.service.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String authHeader = request.getHeader("Authorization");
        
        if (token != null) {
            model.addAttribute("token", token);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            model.addAttribute("token", authHeader.substring(7));
        }
        
        return "dashboard";
    }
    
    @GetMapping("/browse-jobs")
    public String browseJobs(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String authHeader = request.getHeader("Authorization");
        
        if (token != null) {
            model.addAttribute("token", token);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            model.addAttribute("token", authHeader.substring(7));
        }
        
        return "browse-jobs";
    }
    
    @GetMapping("/my-applications")
    public String myApplications(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String authHeader = request.getHeader("Authorization");
        
        if (token != null) {
            model.addAttribute("token", token);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            model.addAttribute("token", authHeader.substring(7));
        }
        
        return "my-applications";
    }
    
    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String authHeader = request.getHeader("Authorization");
        
        if (token != null) {
            model.addAttribute("token", token);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            model.addAttribute("token", authHeader.substring(7));
        }
        
        return "profile";
    }
    
    @GetMapping("/job-details")
    public String jobDetails(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String authHeader = request.getHeader("Authorization");
        
        if (token != null) {
            model.addAttribute("token", token);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            model.addAttribute("token", authHeader.substring(7));
        }
        
        return "job-details";
    }
    
    @GetMapping("/status-demo")
    public String statusDemo() {
        return "status-demo";
    }
}
