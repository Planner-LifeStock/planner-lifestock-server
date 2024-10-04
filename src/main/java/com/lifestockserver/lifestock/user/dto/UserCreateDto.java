package com.lifestockserver.lifestock.user.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class UserCreateDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Length(min = 3, max = 20, message = "아이디는 3 ~ 20자로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Length(min = 6, message = "비밀번호는 최소 6자로 입력해주세요.")
    private String password;

    @NotBlank(message = "실제 이름을 입력해주세요.")
    private String realName;

    private String displayName;  // Optional

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식으로 입력해주세요.")
    private String email;

    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "올바른 전화번호를 입력해주세요.")
    private String phoneNumber;

    //setPhoneNumber은 @Valid에서 자동으로 실행됨.
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.replaceAll("-", "");
    }
}
