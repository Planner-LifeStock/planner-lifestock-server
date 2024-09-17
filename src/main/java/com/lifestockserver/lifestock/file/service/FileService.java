package com.lifestockserver.lifestock.file.service;

import org.springframework.stereotype.Service;

import com.lifestockserver.lifestock.file.repository.FileRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.file.domain.File;
import com.lifestockserver.lifestock.file.dto.FileCreateDto;
import com.lifestockserver.lifestock.common.domain.enums.FileFolder;

@Service
public class FileService {

  private final FileRepository fileRepository;

  public FileService(FileRepository fileRepository) {
    this.fileRepository = fileRepository;
  }

  public File saveFile(MultipartFile file, String meta) {
    File uploadFile = File.builder()
      .originalName(file.getOriginalFilename())
      .folderName(FileFolder.COMPANY)
      .mimeType(file.getContentType())
      .size(file.getSize())
      .meta()
      .build();

    // File file = File.builder()
    //   .originalName(fileCreateDto.getOriginalName())
    //   .folderName(fileCreateDto.getFolderName())
    //   .mimeType(fileCreateDto.getMimeType())
    //   .size(fileCreateDto.getSize())
    //   .meta(fileCreateDto.getMeta())
    //   .build();
    return fileRepository.save(file);
  }

  public File findFileById(Long id) {
    return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
  }

}