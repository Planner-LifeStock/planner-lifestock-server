package com.lifestockserver.lifestock.user.dto;

import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.domain.UserStatus;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String displayName;
    private String phoneNumber;
    private UserStatus status;
    private UserRole role;
}
