package com.lifestockserver.lifestock.user.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class UserCreateDto {

    @NotBlank
    @Length(min = 3, max = 20)
    private String username;

    @NotBlank
    @Length(min = 6)
    private String password;

    @NotBlank
    private String realName;

    private String displayName;  // Optional

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?\\d{10,15}$")
    private String phoneNumber;

}
