package com.lifestockserver.lifestock.file.service;

import org.springframework.stereotype.Service;

import com.lifestockserver.lifestock.file.repository.FileRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.lifestockserver.lifestock.file.domain.File;
import com.lifestockserver.lifestock.file.dto.FileCreateDto;
import com.lifestockserver.lifestock.config.FileConfig;

@Service
public class FileService {

  private final FileRepository fileRepository;

  private final FileConfig fileConfig;

  public FileService(FileRepository fileRepository, FileConfig fileConfig) {
    this.fileRepository = fileRepository;
    this.fileConfig = fileConfig;
  }

  @Transactional
  public File saveFile(FileCreateDto fileCreateDto) {
    String uuid = UUID.randomUUID().toString();
    String path = fileConfig.fileStoragePath + "/" + fileCreateDto.getFolder().getFolderName() + "/" + uuid;

    File uploadFile = File.builder()
      .id(uuid)
      .originalName(fileCreateDto.getFile().getOriginalFilename())
      .folderName(fileCreateDto.getFolder())
      .mimeType(fileCreateDto.getFile().getContentType())
      .size(fileCreateDto.getFile().getSize())
      .meta(fileCreateDto.getMeta())
      .path(path)
      .build();

    try {
      Files.copy(fileCreateDto.getFile().getInputStream(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save file", e);
    }
    return fileRepository.save(uploadFile);
  }

  @Transactional(readOnly = true)
  public File findById(String id) {
    return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
  }

  @Transactional
  public void deleteFile(String id) {
    fileRepository.deleteById(id);
  }
}