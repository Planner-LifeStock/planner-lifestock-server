package com.lifestockserver.lifestock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Value("${server.address}")
  public final String serverHost;

  public AppConfig(@Value("${server.address}") String serverHost) {
    this.serverHost = serverHost;
  }
}