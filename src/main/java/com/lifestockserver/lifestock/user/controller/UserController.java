package com.lifestockserver.lifestock.user.controller;

import com.lifestockserver.lifestock.user.service.UserService;
import com.lifestockserver.lifestock.user.dto.UserRegisterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;  // 인터페이스로 변경

    @PostMapping("/members")
    public String registerMember(@Valid UserRegisterDto userRegisterDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";  // 검증에 실패한 경우 다시 폼으로
        }
        userService.registerMember(userRegisterDto);  // 인터페이스 메서드 호출
        return "redirect:/members";  // 성공 시 회원 목록 페이지로 리디렉션
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }
}
