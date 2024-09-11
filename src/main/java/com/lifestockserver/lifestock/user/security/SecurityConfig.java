package com.lifestockserver.lifestock.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] ADMIN_ENDPOINTS = {"/admin/**"};
    private static final String ROLE_ADMIN = "ADMIN";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF는 개발 단계에서 비활성화합니다
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole(ROLE_ADMIN)  // 상수로 관리하여 하드코딩 방지
                        .anyRequest().permitAll()  // 나머지 모든 요청은 인증 없이 접근 가능
                )
                .httpBasic();  // 기본 인증 방식, 나중에 OAuth2나 JWT로 변경해야 합니다

        return http.build();
    }
}
