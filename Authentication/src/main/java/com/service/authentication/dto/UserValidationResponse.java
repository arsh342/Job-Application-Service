package com.service.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationResponse {
    
    private boolean valid;
    private Long userId;
    private String email;
    private String name;
    private String userType;
    private Long externalUserId;
    private String companyName;
}
