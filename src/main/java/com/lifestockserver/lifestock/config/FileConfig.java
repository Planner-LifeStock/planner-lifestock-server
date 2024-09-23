package com.lifestockserver.lifestock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FileConfig {
  public final String fileStoragePath;

  public final String fileGetPath;

  public final String defaultLogoPath;

  public final String defaultProfilePath;

  public final String defaultLogoName;

  public final String defaultProfileName;
  
  public FileConfig(
    @Value("${upload.file.path}") String fileStoragePath,
    @Value("${get.file.path}") String fileGetPath,
    @Value("${company.default.logo.path}") String defaultLogoPath,
    @Value("${user.default.profile.path}") String defaultProfilePath,
    @Value("${company.default.logo.name}") String defaultLogoName,
    @Value("${user.default.profile.name}") String defaultProfileName) {
    this.fileStoragePath = fileStoragePath;
    this.fileGetPath = fileGetPath;
    this.defaultLogoPath = defaultLogoPath;
    this.defaultProfilePath = defaultProfilePath;
    this.defaultLogoName = defaultLogoName;
    this.defaultProfileName = defaultProfileName;
  }
}