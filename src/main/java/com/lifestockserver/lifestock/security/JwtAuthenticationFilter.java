package com.lifestockserver.lifestock.security;

import com.lifestockserver.lifestock.auth.service.AuthService;
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
import jakarta.inject.Provider;
import org.springframework.lang.NonNull;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Provider<AuthService> authServiceProvider;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(Provider<AuthService> authServiceProvider) {
        this.authServiceProvider = authServiceProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        // 토큰이 유효하면 SecurityContext에 저장
        if (token != null && authServiceProvider.get().validateToken(token)) {
            Authentication auth = authServiceProvider.get().getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            return ;
            // throw new ServletException("토큰이 유효하지 않습니다.");
        }
        
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return request.getRequestURI().equals("/auth/login") ||
            request.getRequestURI().equals("/user/register");
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
        if (!authServiceProvider.get().validateToken(bearerToken.substring(7))) {
            logger.warn("유효하지 않은 토큰입니다.");
            return null;
        }

        // "BEARER " 제거
        return bearerToken.substring(7);
    }
}
