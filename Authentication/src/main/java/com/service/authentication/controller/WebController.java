package com.service.authentication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "success", required = false) String success,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String register(@RequestParam(value = "error", required = false) String error,
                          @RequestParam(value = "success", required = false) String success,
                          Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "register";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}
