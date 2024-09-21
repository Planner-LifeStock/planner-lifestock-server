package com.lifestockserver.lifestock.file.domain;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.config.AppConfig;
import com.lifestockserver.lifestock.common.domain.enums.FileFolder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class File extends Base {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String originalName;

  @Enumerated(EnumType.STRING)
  private FileFolder folderName;
  private String path;

  private String mimeType;
  private Long size; // bytes 단위 사이즈

  @Column(nullable = true)
  private String meta;

  @Transient
  private String url;

  @Transient
  private static ApplicationContext context;

  public static void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
    File.context = applicationContext;
  }

  @PostLoad
  public void generateUrl() {
    AppConfig appConfig = context.getBean(AppConfig.class);
    this.url = appConfig.serverHost + "/" + this.folderName + "/" + id;
  }
}