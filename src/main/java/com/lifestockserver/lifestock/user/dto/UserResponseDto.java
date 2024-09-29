package com.lifestockserver.lifestock.user.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String displayName;
    private String phoneNumber;
    private String status;
    private String role;
}
