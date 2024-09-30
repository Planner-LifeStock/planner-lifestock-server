package com.lifestockserver.lifestock.security;

import com.lifestockserver.lifestock.auth.service.AuthServiceImpl;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Pointcut;

@Component
@Aspect
public class AuthAspect {

    private final AuthServiceImpl authServiceImpl;
    private final HttpServletRequest request;

    public AuthAspect(AuthServiceImpl authServiceImpl, HttpServletRequest request) {
        this.authServiceImpl = authServiceImpl;
        this.request = request;
    }

    @Pointcut("@annotation(com.lifestockserver.lifestock.security.Auth)")
    public void authPointcut() {}

    @Before("authPointcut()")
    public void validateToken() {
        String token = getBearerTokenFromRequest(request);
        if (token == null || !authServiceImpl.validateToken(token)) {
            throw new SecurityException("잘못된 토큰입니다.");
        }

        Authentication auth = authServiceImpl.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getBearerTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
