package com.lifestockserver.lifestock.auth.controller;

import com.lifestockserver.lifestock.auth.dto.LoginRequestDto;
import com.lifestockserver.lifestock.auth.dto.TokenResponseDto;
import com.lifestockserver.lifestock.auth.service.AuthService;
import com.lifestockserver.lifestock.user.domain.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequestDto loginRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "login";
        }

        UserRole userRole = authService.getUserRoleByUsername(loginRequest.getUsername());
        String accessToken = authService.createAccessToken(loginRequest.getUsername(), userRole);
        String refreshToken = authService.createRefreshToken(loginRequest.getUsername());

        // 토큰 발행 후 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    // 로그인 페이지 출력
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 리프레시 토큰 처리
    @PostMapping("/refresh")
    public String refresh(@RequestHeader("Authorization") String refreshToken, HttpServletResponse response) {
        authService.refresh(refreshToken, response);
        return "redirect:/";
    }

    // 토큰 검증 처리
    @GetMapping("/validate")
    public String validateToken(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        authService.validateToken(token, response);
        return "redirect:/";
    }
}
