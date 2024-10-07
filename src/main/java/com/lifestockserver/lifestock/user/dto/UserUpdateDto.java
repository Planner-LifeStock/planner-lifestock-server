package com.lifestockserver.lifestock.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDto {

    private Long id;

    private String realName;

    private String displayName;

    @Email(message = "올바른 이메일 형식으로 입력해주세요.")
    private String email;

    private String phoneNumber;
}
