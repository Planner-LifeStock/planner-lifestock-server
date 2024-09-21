package com.lifestockserver.lifestock.file.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.*;

import com.lifestockserver.lifestock.common.domain.enums.FileFolder;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileCreateDto {
  private MultipartFile file;
  private String meta;
  private FileFolder folder;
}