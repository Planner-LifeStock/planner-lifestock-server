package com.lifestockserver.lifestock.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "아이디는 빈칸일 수 없어요!")
    private String username;

    @NotBlank(message = "비밀번호는 빈칸일 수 없어요!")
    private String password;
}