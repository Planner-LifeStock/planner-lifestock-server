package com.lifestockserver.lifestock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Value("${server.address}")
  private String serverHost;

  public String getServerHost() {
    return serverHost;
  }
}