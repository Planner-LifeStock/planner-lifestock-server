package com.lifestockserver.lifestock.auth.controller;

import com.lifestockserver.lifestock.auth.dto.LoginRequestDto;
import com.lifestockserver.lifestock.auth.dto.TokenResponseDto;
import com.lifestockserver.lifestock.auth.exception.InvalidTokenException;
import com.lifestockserver.lifestock.auth.service.AuthService;
import com.lifestockserver.lifestock.user.domain.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        UserRole userRole = authService.getUserRoleByUsername(loginRequest.getUsername());
        String accessToken = authService.createAccessToken(loginRequest.getUsername(), userRole);
        String refreshToken = authService.createRefreshToken(loginRequest.getUsername());

        return ResponseEntity.ok(new TokenResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestHeader("Authorization") String refreshToken) {
        if (authService.validateToken(refreshToken)) {
            String username = authService.getUsernameFromToken(refreshToken);
            String newAccessToken = authService.createAccessToken(username, authService.getUserRoleByUsername(username));

            return ResponseEntity.ok(new TokenResponseDto(newAccessToken, refreshToken));
        } else {
            throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다.");
        }
    }

    //토큰 검증하는곳
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok("유효한 토큰입니다.");
        } else {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }
}
