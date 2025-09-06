package com.service.authentication.dto;

import com.service.authentication.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String email;
    private UserType userType;
    private Long externalUserId;
    private String companyName;
}
