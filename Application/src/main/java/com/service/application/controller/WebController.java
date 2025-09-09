package com.service.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/browse-jobs")
    public String browseJobs() {
        return "browse-jobs";
    }
    
    @GetMapping("/my-applications")
    public String myApplications() {
        return "my-applications";
    }
    
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
    
    @GetMapping("/job-details")
    public String jobDetails() {
        return "job-details";
    }
    
    @GetMapping("/status-demo")
    public String statusDemo() {
        return "status-demo";
    }
}
