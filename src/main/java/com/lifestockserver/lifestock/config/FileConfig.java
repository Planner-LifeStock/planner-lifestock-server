package com.lifestockserver.lifestock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FileConfig {
  public final String defaultLogoName;
  public final String defaultProfileName;
  
  public FileConfig(
    @Value("${company.default.logo.name}") String defaultLogoName,
    @Value("${user.default.profile.name}") String defaultProfileName) {
    this.defaultLogoName = defaultLogoName;
    this.defaultProfileName = defaultProfileName;
  }
}
