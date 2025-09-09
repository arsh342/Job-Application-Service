package com.service.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8083}")
public interface AuthServiceClient {
    
    @GetMapping("/api/auth/users/{userId}")
    UserDto getUserById(@PathVariable("userId") Long userId);
}
