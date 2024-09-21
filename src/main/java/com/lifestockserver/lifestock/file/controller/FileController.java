package com.lifestockserver.lifestock.file.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;  
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.lifestockserver.lifestock.file.dto.FileCreateDto;
import com.lifestockserver.lifestock.file.service.FileService;
import com.lifestockserver.lifestock.file.domain.File;
import com.lifestockserver.lifestock.common.domain.enums.FileFolder;
@RestController
@RequestMapping("/files")
public class FileController {

  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public File createFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "meta", required = false) String meta, @RequestParam(value = "folder", required = false) FileFolder folder) {
    FileCreateDto fileCreateDto = FileCreateDto.builder()
      .file(file)
      .meta(meta)
      .folder(folder != null? folder : FileFolder.DEFAULT)
      .build();
    
    return fileService.saveFile(fileCreateDto);
  }

  @GetMapping("/{id}")
  public File getFile(@PathVariable String id) {
    return fileService.findById(id);
  }
}