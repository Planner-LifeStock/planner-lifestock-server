package com.lifestockserver.lifestock.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    private final String secretKey = "1234qwer";
    private final long expirationTime = 1000 * 60 * 60;

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = loadUserDetailsFromToken(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String createAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())  // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 서명 알고리즘
                .compact();
    }

    private UserDetails loadUserDetailsFromToken(String token) {
        // 실제 구현에 따라 DB에서 유저 정보를 로드하는 로직이 더 필요합니다!
        return null;
    }
}
