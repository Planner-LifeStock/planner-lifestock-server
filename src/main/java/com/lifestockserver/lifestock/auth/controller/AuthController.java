package com.lifestockserver.lifestock.auth.controller;

import com.lifestockserver.lifestock.auth.dto.LoginRequestDto;
import com.lifestockserver.lifestock.auth.dto.TokenResponseDto;
import com.lifestockserver.lifestock.auth.exception.InvalidTokenException;
import com.lifestockserver.lifestock.auth.service.TokenService;
import com.lifestockserver.lifestock.user.domain.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        String accessToken = tokenService.createAccessToken(loginRequest.getUsername(), UserRole.USER.name());
        String refreshToken = tokenService.createRefreshToken(loginRequest.getUsername());

        return ResponseEntity.ok(new TokenResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestHeader("Authorization") String refreshToken) {
        if (tokenService.validateToken(refreshToken)) {
            String username = tokenService.getUsernameFromToken(refreshToken);
            String newAccessToken = tokenService.createAccessToken(username, UserRole.USER.name());

            return ResponseEntity.ok(new TokenResponseDto(newAccessToken, refreshToken));
        } else {
            throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다.");
        }
    }
}