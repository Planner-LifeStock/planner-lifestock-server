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

  @Value("${spring.servlet.multipart.location}")
  private String fileStoragePath;

  public String getFileStoragePath() {
    return fileStoragePath;
  }

  @Value("${company.default.logo.path}")
  private String defaultLogoPath;

  public String getDefaultLogoPath() {
    return defaultLogoPath;
  }

  @Value("${user.default.profile.path}")
  private String defaultProfilePath;

  public String getDefaultProfilePath() {
    return defaultProfilePath;
  }
}