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
import jakarta.inject.Provider;
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

    private final Provider<UserService> userServiceProvider;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final String secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public AuthServiceImpl(
            Provider<UserService> userServiceProvider,
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration-time}") long accessExpirationTime,
            @Value("${jwt.refresh-token-expiration-time}") long refreshExpirationTime
    ) {
        this.userServiceProvider = userServiceProvider;
        this.secretKey = secretKey;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = loadUserDetailsFromToken(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public UserDetails loadUserDetailsFromToken(String token) {
        String username = getUsernameFromToken(token);
        return userServiceProvider.get().loadUserByUsername(username);
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("만료된 토큰: {}", token);
            return false;
        } catch (Exception e) {
            logger.error("잘못된 토큰: {}", token, e);
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
        return userServiceProvider.get().findUserByUsername(username)
                .map(User::getRole)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequest) {
        User user = userServiceProvider.get().findUserByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserRole userRole = user.getRole();
        String accessToken = createAccessToken(user.getUsername(), userRole);
        String refreshToken = createRefreshToken(user.getUsername());

        return new TokenResponseDto(accessToken, refreshToken);
    }

    @Override
    public TokenResponseDto refresh(String refreshToken) {
        if (validateToken(refreshToken)) {
            String username = getUsernameFromToken(refreshToken);
            String newAccessToken = createAccessToken(username, getUserRoleByUsername(username));

            return new TokenResponseDto(newAccessToken, refreshToken);
        } else {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
    }
}
