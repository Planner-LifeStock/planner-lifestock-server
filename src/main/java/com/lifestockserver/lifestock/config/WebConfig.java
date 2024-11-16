package com.lifestockserver.lifestock.config;

import java.rmi.registry.Registry;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // registry.addMapping("/**").allowedOrigins("*")
        registry.addMapping("/**")
                .allowedOrigins("https://life-stock.vercel.app", "http://localhost:5173",
                        "https://www.lifestock.store/")
                .allowedMethods("GET", "POST", "PUT", "DELETE").allowCredentials(true);
    }
}
