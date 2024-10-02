package com.lifestockserver.lifestock.file.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.lifestockserver.lifestock.file.repository.FileRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.io.IOException;
import java.time.LocalDateTime;

import com.lifestockserver.lifestock.file.domain.File;
import com.lifestockserver.lifestock.file.dto.FileCreateDto;
import com.lifestockserver.lifestock.config.FileConfig;
import com.lifestockserver.lifestock.file.dto.FileResponseDto;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
@Transactional(readOnly = true)
public class FileService {

  private final FileRepository fileRepository;
  private final AmazonS3 amazonS3;
  private final FileConfig fileConfig;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public FileService(FileRepository fileRepository, FileConfig fileConfig, AmazonS3 amazonS3) {
    this.fileRepository = fileRepository;
    this.fileConfig = fileConfig;
    this.amazonS3 = amazonS3;
  }

  @Transactional
  public FileResponseDto saveFile(FileCreateDto fileCreateDto) {
    String uuid = UUID.randomUUID().toString();
    String originalFilename = fileCreateDto.getFile().getOriginalFilename();
    if (originalFilename == null) {
      throw new RuntimeException("originalFilename is null");
    }

    String fileName = fileCreateDto.getFolder().getFolderName() + "/" + uuid;

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(fileCreateDto.getFile().getSize());
    metadata.setContentType(fileCreateDto.getFile().getContentType());

    try {
      amazonS3.putObject(bucket, fileName, fileCreateDto.getFile().getInputStream(), metadata);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save file to s3", e);
    }

    String url = amazonS3.getUrl(bucket, fileName).toString();

    File uploadFile = File.builder()
      .id(uuid)
      .originalName(originalFilename)
      .folderName(fileCreateDto.getFolder())
      .fileName(fileName)
      .mimeType(fileCreateDto.getFile().getContentType())
      .size(fileCreateDto.getFile().getSize())
      .meta(fileCreateDto.getMeta())
      .url(url)
      .build();

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

  public FileResponseDto findById(String id) {
    File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
    return FileResponseDto.builder()
      .id(file.getId())
      .fileName(file.getFileName())
      .originalName(file.getOriginalName())
      .mimeType(file.getMimeType())
      .size(file.getSize())
      .meta(file.getMeta())
      .url(file.getUrl())
      .build();
  }

  public File getFileById(String id) {
    return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
  }

  public File getDefaultCompanyLogo() {
    return fileRepository.findById(fileConfig.defaultLogoName).orElseThrow(() -> new RuntimeException("File not found"));
  }

  public File getDefaultUserProfile() {
    return fileRepository.findById(fileConfig.defaultProfileName).orElseThrow(() -> new RuntimeException("File not found"));
  }

  @Transactional
  public void deleteFile(String id) {
    File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));

    file.setDeletedAt(LocalDateTime.now());
  }
}