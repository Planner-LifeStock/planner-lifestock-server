package com.lifestockserver.lifestock.file.service;

import org.springframework.stereotype.Service;

import com.lifestockserver.lifestock.file.repository.FileRepository;

import org.springframework.transaction.annotation.Transactional;

import com.lifestockserver.lifestock.file.domain.File;
import com.lifestockserver.lifestock.file.dto.FileCreateDto;

@Service
public class FileService {

  private final FileRepository fileRepository;

  public FileService(FileRepository fileRepository) {
    this.fileRepository = fileRepository;
  }

  @Transactional
  public File saveFile(FileCreateDto fileCreateDto) {
    File uploadFile = File.builder()
      .originalName(fileCreateDto.getFile().getOriginalFilename())
      .folderName(fileCreateDto.getFolder())
      .mimeType(fileCreateDto.getFile().getContentType())
      .size(fileCreateDto.getFile().getSize())
      .meta(fileCreateDto.getMeta())
      .build();

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