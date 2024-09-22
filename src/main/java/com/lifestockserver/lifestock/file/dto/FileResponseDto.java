package com.lifestockserver.lifestock.file.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponseDto {
  private String id;
  private String originalName;
  private String mimeType;
  private Long size;
  private String meta;
  private String url;
}
