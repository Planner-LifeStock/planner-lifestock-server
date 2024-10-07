package com.lifestockserver.lifestock.auth.service;

import com.lifestockserver.lifestock.auth.dto.LoginRequestDto;
import com.lifestockserver.lifestock.auth.dto.TokenResponseDto;
import com.lifestockserver.lifestock.user.domain.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    String createAccessToken(String username, UserRole role);
    String createRefreshToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    Authentication getAuthentication(String token);
    UserRole getUserRoleByUsername(String username);
    UserDetails loadUserDetailsFromToken(String token);
    TokenResponseDto login(LoginRequestDto loginRequest);
    TokenResponseDto refresh(String refreshToken);
}