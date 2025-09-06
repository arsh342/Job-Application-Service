package com.service.application.client;

import com.service.application.dto.JobDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "job-service", url = "${job.service.url:http://localhost:8081}")
public interface JobServiceClient {
    
    @GetMapping("/api/jobs/{jobId}")
    JobDto getJobById(@PathVariable("jobId") Long jobId);
}
