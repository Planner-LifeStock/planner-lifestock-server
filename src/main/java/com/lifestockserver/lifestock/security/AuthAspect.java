package com.lifestockserver.lifestock.security;

import com.lifestockserver.lifestock.auth.service.AuthServiceImpl;
import com.lifestockserver.lifestock.user.domain.UserRole;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

@Component
@Aspect
public class AuthAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuthAspect.class);

    private final AuthServiceImpl authServiceImpl;
    private final HttpServletRequest request;

    public AuthAspect(AuthServiceImpl authServiceImpl, HttpServletRequest request) {
        this.authServiceImpl = authServiceImpl;
        this.request = request;
    }

    @Pointcut("@annotation(com.lifestockserver.lifestock.security.Auth)")
    public void authPointcut() {}

    @Before("@annotation(auth)")
    public void validateToken(Auth auth) {
        String token = getBearerTokenFromRequest(request);

        if (token == null) {
            logger.warn(() -> "Authorization 헤더에 토큰이 존재하지 않습니다.");
            throw new AuthenticationException("유효하지 않은 토큰입니다.") {};
        }

        if (!authServiceImpl.validateToken(token)) {
            logger.warn(() -> "유효하지 않은 토큰입니다: " + token);
            throw new AuthenticationException("유효하지 않은 토큰입니다.") {};
        }

        Authentication authentication = authServiceImpl.getAuthentication(token);

        UserRole[] requiredRoles = auth.roles();
        if (requiredRoles.length > 0) {
            boolean hasRequiredRole = Arrays.stream(requiredRoles)
                    .anyMatch(role -> authentication.getAuthorities().contains(new SimpleGrantedAuthority(role.name())));
            if (!hasRequiredRole) {
                logger.warn(() -> "권한이 없는 사용자입니다. 토큰: " + token);
                throw new AccessDeniedException("권한이 없습니다.");
            }
        }

        logger.info(() -> "유효한 토큰으로 인증됨: " + token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private String getBearerTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
