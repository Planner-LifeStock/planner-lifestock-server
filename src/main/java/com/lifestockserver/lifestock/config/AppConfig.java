package com.lifestockserver.lifestock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Value("${server.address}")
  public final String serverHost;

  @Value("${server.port}")
  public final String serverPort;

  public AppConfig(@Value("${server.address}") String serverHost, @Value("${server.port}") String serverPort) {
    this.serverHost = serverHost;
    this.serverPort = serverPort;
  }
}