package com.lifestockserver.lifestock.file.domain;

import org.springframework.stereotype.Component;

import com.lifestockserver.lifestock.common.domain.Base;
import com.lifestockserver.lifestock.common.domain.enums.FileFolder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Component
public class File extends Base {
  @Id
  private String id;

  private String originalName;

  @Enumerated(EnumType.STRING)
  private FileFolder folderName;
  private String fileName;

  private String mimeType;
  private Long size; // bytes 단위 사이즈

  @Column(nullable = true)
  private String meta;

  private String url;
}