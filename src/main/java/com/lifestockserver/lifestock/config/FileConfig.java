package com.lifestockserver.lifestock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FileConfig {
  public final String fileStoragePath;

  public final String defaultLogoPath;

  public final String defaultProfilePath;

  public FileConfig(
    @Value("${upload.file.path}") String fileStoragePath,
    @Value("${company.default.logo.path}") String defaultLogoPath,
    @Value("${user.default.profile.path}") String defaultProfilePath) {
    this.fileStoragePath = fileStoragePath;
    this.defaultLogoPath = defaultLogoPath;
    this.defaultProfilePath = defaultProfilePath;
  }
}
