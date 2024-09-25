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
import com.lifestockserver.lifestock.file.dto.FileResponseDto;

@Service
public class FileService {

  private final FileRepository fileRepository;

  private final FileConfig fileConfig;

  public FileService(FileRepository fileRepository, FileConfig fileConfig) {
    this.fileRepository = fileRepository;
    this.fileConfig = fileConfig;
  }

  @Transactional
  public FileResponseDto saveFile(FileCreateDto fileCreateDto) {
    String uuid = UUID.randomUUID().toString();
    String originalFilename = fileCreateDto.getFile().getOriginalFilename();
    if (originalFilename == null) {
      throw new RuntimeException("originalFilename is null");
    }
    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
    String path = fileConfig.fileStoragePath + "/" + fileCreateDto.getFolder().getFolderName() + "/" + uuid + fileExtension;

    File uploadFile = File.builder()
      .id(uuid)
      .originalName(originalFilename)
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

    File savedFile = fileRepository.save(uploadFile);

    FileResponseDto fileResponseDto = FileResponseDto.builder()
      .id(savedFile.getId())
      .originalName(savedFile.getOriginalName())
      .mimeType(savedFile.getMimeType())
      .size(savedFile.getSize())
      .meta(savedFile.getMeta())
      .url(savedFile.getUrl())
      .build();
    return fileResponseDto;
  }

  @Transactional(readOnly = true)
  public FileResponseDto findById(String id) {
    File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
    return FileResponseDto.builder()
      .id(file.getId())
      .originalName(file.getOriginalName())
      .mimeType(file.getMimeType())
      .size(file.getSize())
      .meta(file.getMeta())
      .url(file.getUrl())
      .build();
  }

  @Transactional(readOnly = true)
  public File getFileById(String id) {
    return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
  }

  @Transactional(readOnly = true)
  public File getDefaultCompanyLogo() {
    return fileRepository.findById(fileConfig.defaultLogoName).orElseThrow(() -> new RuntimeException("File not found"));
  }

  @Transactional(readOnly = true)
  public File getDefaultUserProfile() {
    return fileRepository.findById(fileConfig.defaultProfileName).orElseThrow(() -> new RuntimeException("File not found"));
  }

  @Transactional
  public void deleteFile(String id) {
    fileRepository.deleteById(id);
  }
}