package com.lifestockserver.lifestock.security;

import com.lifestockserver.lifestock.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    public JwtAuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        // 토큰이 유효하면 SecurityContext에 저장
        if (token != null && authService.validateToken(token)) {
            Authentication auth = authService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null) {
            logger.warn("Authorization 헤더가 존재하지 않습니다.");
            return null;
        }
        if (!bearerToken.startsWith("Bearer ")) {
            logger.warn("Authorization 헤더의 형식이 올바르지 않습니다. 토큰이 'Bearer '로 시작해야 합니다.");
            return null;
        }

        // "BEARER " 제거
        return bearerToken.substring(7);
    }
}
