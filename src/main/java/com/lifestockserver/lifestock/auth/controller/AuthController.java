package com.lifestockserver.lifestock.auth.controller;

import com.lifestockserver.lifestock.auth.dto.LoginRequestDto;
import com.lifestockserver.lifestock.auth.dto.TokenResponseDto;
import com.lifestockserver.lifestock.auth.service.TokenService;
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
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest) {
        String accessToken = tokenService.createAccessToken(loginRequest.getUsername(), "ROLE_USER");
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestHeader("Authorization") String refreshToken) {
        if (tokenService.validateToken(refreshToken)) {
            String username = tokenService.getUsernameFromToken(refreshToken);
            String newAccessToken = tokenService.createAccessToken(username, "ROLE_USER");

            return ResponseEntity.ok(new TokenResponseDto(newAccessToken, refreshToken));
        } else {
            return ResponseEntity.status(403).body(null);
        }
    }
}
