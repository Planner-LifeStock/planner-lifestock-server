package com.lifestockserver.lifestock.file.domain;

import org.springframework.beans.factory.annotation.Autowired;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.config.AppConfig;
import com.lifestockserver.lifestock.common.domain.enums.FileFolder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class File extends Base {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String originalName;
  @Enumerated(EnumType.STRING)
  private FileFolder folderName;
  private String path;

  private String mimeType;
  private Long size; // bytes 단위 사이즈

  @Column(nullable = true)
  private Object meta;
  @Transient
  private String url;

  @Autowired
  @Transient
  AppConfig appConfig;

  @PrePersist
  @PreUpdate
  private void setFolderName() {
    this.path = appConfig.getFileStoragePath() + "/" + FileFolder.FILE.getFolderName() + "/" + this.id;
  }

  @PostLoad
  private void generateUrl() {
    this.url = appConfig.getServerHost() + "/" + this.path;
  }

}