package com.lifestockserver.lifestock.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

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
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole(ROLE_ADMIN)
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults());  //OAuth2 방식으로 나중에 바꿀 수 있습니다

        return http.build();
    }
}
