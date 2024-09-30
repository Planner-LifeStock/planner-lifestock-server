package com.lifestockserver.lifestock.auth.service;

import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final String secretKey;
    private final long expirationTime;

    public AuthServiceImpl(
            UserService userService,  // 필드 주입
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration-time}") long expirationTime
    ) {
        this.userService = userService;
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = loadUserDetailsFromToken(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired: {}", token);
            return false;
        } catch (Exception e) {
            logger.error("Invalid token: {}", token, e);
            return false;
        }
    }

    @Override
    public String createAccessToken(String username, UserRole role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public String createRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private UserDetails loadUserDetailsFromToken(String token) {
        // JWT 토큰을 통해 사용자 정보를 로드하는 로직을 구현해야 함
        return null;
    }

    @Override
    public UserRole getUserRoleByUsername(String username) {
        return userService.findUserByUsername(username)
                .map(user -> user.getRole())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
    }
}
