package com.lifestockserver.lifestock.auth.service;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String createAccessToken(String username, String role);
    String createRefreshToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    Authentication getAuthentication(String token);
}
