package com.service.job.client;

import com.service.job.dto.ApplicationDto;
import com.service.job.dto.UserProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "application-service", url = "${application.service.url:http://localhost:8082}")
public interface ApplicationServiceClient {
    
    @GetMapping("/api/jobs/{jobId}/applications")
    List<ApplicationDto> getApplicationsForJob(@PathVariable("jobId") Long jobId);
    
    @GetMapping("/api/applicants/{applicantId}")
    UserProfileDto getApplicantProfile(@PathVariable("applicantId") Long applicantId);
}
