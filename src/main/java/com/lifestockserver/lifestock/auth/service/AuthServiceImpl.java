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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            Provider<UserService> userServiceProvider,
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration-time}") long accessExpirationTime,
            @Value("${jwt.refresh-token-expiration-time}") long refreshExpirationTime,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userServiceProvider = userServiceProvider;
        this.secretKey = secretKey;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        UserDetails userDetails = userServiceProvider.get().loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error("잘못된 형식의 토큰: {}", token);
        } catch (io.jsonwebtoken.SignatureException e) {
            logger.error("서명 검증 실패: {}", token);
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            logger.error("지원하지 않는 토큰: {}", token);
        } catch (IllegalArgumentException e) {
            logger.error("토큰이 비어있거나 잘못되었습니다: {}", token);
        } catch (Exception e) {
            logger.error("알 수 없는 오류로 인해 토큰이 유효하지 않습니다: {}", token, e);
        }
        return false;
    }

    @Override
    public String createAccessToken(String username, Long userId, UserRole role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
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
    public TokenResponseDto login(LoginRequestDto loginRequest) {
        User user = userServiceProvider.get().findUserByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = createAccessToken(user.getUsername(), user.getId(), user.getRole());
        String refreshToken = createRefreshToken(user.getUsername());

        return new TokenResponseDto(accessToken, refreshToken);
    }

    @Override
    public TokenResponseDto refresh(String refreshToken) {
        if (validateToken(refreshToken)) {
            String username = getUsernameFromToken(refreshToken);
            User user = userServiceProvider.get().findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            String newAccessToken = createAccessToken(username, user.getId(), user.getRole());

            return new TokenResponseDto(newAccessToken, refreshToken);
        } else {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
    }
}
