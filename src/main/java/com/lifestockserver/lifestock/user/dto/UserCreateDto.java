package com.lifestockserver.lifestock.user.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class UserCreateDto {

    @NotBlank(message = ValidationMessages.USERNAME_EMPTY)
    @Length(min = 3, max = 20, message = ValidationMessages.USERNAME_LENGTH)
    private String username;

    @NotBlank(message = ValidationMessages.PASSWORD_EMPTY)
    @Length(min = 6, message = ValidationMessages.PASSWORD_LENGTH)
    private String password;

    @NotBlank(message = ValidationMessages.REAL_NAME_EMPTY)
    private String realName;

    private String displayName;  // Optional

    @NotBlank(message = ValidationMessages.EMAIL_EMPTY)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    private String email;

    @NotBlank(message = ValidationMessages.PHONE_NUMBER_EMPTY)
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = ValidationMessages.PHONE_NUMBER_INVALID)
    private String phoneNumber;

}
