package com.service.job.controller;

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
    
    @GetMapping("/login-redirect")
    public String loginRedirect() {
        return "redirect:http://localhost:8083/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/job-listings")
    public String jobListings() {
        return "job-listings";
    }
    
    @GetMapping("/jobs")
    public String jobs(HttpServletRequest request, Model model) {
        // Add user information from request attributes
        model.addAttribute("userName", request.getAttribute("userName"));
        model.addAttribute("userType", request.getAttribute("userType"));
        return "jobs";
    }
    
    @GetMapping("/create-job")
    public String createJob(HttpServletRequest request, Model model) {
        model.addAttribute("userName", request.getAttribute("userName"));
        model.addAttribute("userType", request.getAttribute("userType"));
        return "create-job";
    }
    
    @GetMapping("/job-details")
    public String jobDetails() {
        return "job-details";
    }
    
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
    
    @GetMapping("/debug")
    public String debug() {
        return "debug";
    }
}
