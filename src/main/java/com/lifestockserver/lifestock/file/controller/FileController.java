package com.lifestockserver.lifestock.file.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;  
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import com.lifestockserver.lifestock.file.dto.FileResponseDto;
import com.lifestockserver.lifestock.file.dto.FileCreateDto;
import com.lifestockserver.lifestock.file.service.FileService;
import com.lifestockserver.lifestock.common.domain.enums.FileFolder;
@RestController
@RequestMapping("/files")
public class FileController {

  private final FileService fileService;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileResponseDto> createFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "meta", required = false) String meta, @RequestParam(value = "folder", required = false) String folder) {
    FileFolder fileFolder;
    if (folder != null) {
      fileFolder = FileFolder.valueOf(folder);
    } else {
      fileFolder = FileFolder.DEFAULT;
    }
    
    FileCreateDto fileCreateDto = FileCreateDto.builder()
      .file(file)
      .meta(meta)
      .folder(fileFolder)
      .build();

    return ResponseEntity.ok().body(fileService.saveFile(fileCreateDto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<FileResponseDto> getFile(@PathVariable String id) {
    return ResponseEntity.ok().body(fileService.findById(id));
  }
}