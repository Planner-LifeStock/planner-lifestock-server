package com.lifestockserver.lifestock.file.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;  
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.lifestockserver.lifestock.file.dto.FileCreateDto;
import com.lifestockserver.lifestock.file.service.FileService;
import com.lifestockserver.lifestock.file.domain.File;

@RestController
@RequestMapping("/files")
public class FileController {

  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public File createFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "meta", required = false) String meta) {
    return fileService.saveFile(file, meta);
  }

  @GetMapping("/{id}")
  public File getFile(@PathVariable Long id) {
    return fileService.findFileById(id);
  }
}