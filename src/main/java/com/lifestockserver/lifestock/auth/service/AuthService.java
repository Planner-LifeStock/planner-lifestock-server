package com.lifestockserver.lifestock.auth.service;

import com.lifestockserver.lifestock.auth.dto.LoginRequestDto;
import com.lifestockserver.lifestock.auth.dto.TokenResponseDto;
import com.lifestockserver.lifestock.user.domain.UserRole;
import org.springframework.security.core.Authentication;

public interface AuthService {
    String createAccessToken(String username, Long userId, UserRole role);
    String createRefreshToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    Authentication getAuthentication(String token);
    TokenResponseDto login(LoginRequestDto loginRequest);
    TokenResponseDto refresh(String refreshToken);
}
