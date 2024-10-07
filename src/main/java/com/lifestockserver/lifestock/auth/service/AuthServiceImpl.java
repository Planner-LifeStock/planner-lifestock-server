package com.lifestockserver.lifestock.auth.service;

import com.lifestockserver.lifestock.auth.dto.LoginRequestDto;
import com.lifestockserver.lifestock.auth.dto.TokenResponseDto;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
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
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public AuthServiceImpl(
            UserService userService,
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration-time}") long accessExpirationTime,
            @Value("${jwt.refresh-token-expiration-time}") long refreshExpirationTime
    ) {
        this.userService = userService;
        this.secretKey = secretKey;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = accessExpirationTime;
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = loadUserDetailsFromToken(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public UserDetails loadUserDetailsFromToken(String token) {
        String username = getUsernameFromToken(token);
        return userService.loadUserByUsername(username);
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();  // 토큰에서 사용자명 추출
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
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public String createRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public UserRole getUserRoleByUsername(String username) {
        return userService.findUserByUsername(username)
                .map(user -> user.getRole())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequest) {
        User user = userService.findUserByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserRole userRole = user.getRole();
        String accessToken = createAccessToken(user.getUsername(), userRole);
        String refreshToken = createRefreshToken(user.getUsername());

        return new TokenResponseDto(accessToken, refreshToken);
    }

    @Override
    public void refresh(String refreshToken, HttpServletResponse response) {
        if (validateToken(refreshToken)) {
            String username = getUsernameFromToken(refreshToken);
            String newAccessToken = createAccessToken(username, getUserRoleByUsername(username));

            response.setHeader("Authorization", "Bearer " + newAccessToken);
        } else {
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }
}
