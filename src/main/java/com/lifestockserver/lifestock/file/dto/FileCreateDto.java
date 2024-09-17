package com.lifestockserver.lifestock.file.dto;

import lombok.*;

import com.lifestockserver.lifestock.common.domain.enums.FileFolder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileCreateDto {
  private String originalName;
  private FileFolder folderName;
  private String mimeType;
  private Long size;
  private String meta;
}