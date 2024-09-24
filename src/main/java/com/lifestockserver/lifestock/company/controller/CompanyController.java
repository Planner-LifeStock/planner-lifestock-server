package com.lifestockserver.lifestock.company.controller;

import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.dto.CompanyDeleteDto;
import com.lifestockserver.lifestock.company.service.CompanyService;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import com.lifestockserver.lifestock.company.dto.CompanyUpdateDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

@RestController
@RequestMapping("/company")
@Tag(name = "Company API", description = "생성한 company에 대한 컨트롤러")
public class CompanyController {

  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  
  @GetMapping
  public ResponseEntity<List<CompanyResponseDto>> getCompaniesByUserId(@RequestParam(required = true, value="userId") Long userId) {
    List<CompanyResponseDto> companyResponseDtos = companyService.findAllByUserId(userId);
    return ResponseEntity.ok(companyResponseDtos);
  }
  
  @GetMapping("/{companyId}")
  public ResponseEntity<CompanyResponseDto> getCompany(@PathVariable Long companyId) {
    CompanyResponseDto companyResponseDto = companyService.findById(companyId);
    return ResponseEntity.ok(companyResponseDto);
  }
  
  @PostMapping
  public ResponseEntity<CompanyResponseDto> createCompany(@RequestBody CompanyCreateDto companyCreateDto, String userId) {
    CompanyResponseDto companyResponseDto = companyService.createCompany(companyCreateDto);
    return ResponseEntity.ok(companyResponseDto);
  }

  @PutMapping("/{companyId}")
  public ResponseEntity<CompanyResponseDto> updateCompany(@PathVariable Long companyId, @RequestBody CompanyUpdateDto companyUpdateDto) {
    CompanyResponseDto companyResponseDto = companyService.updateCompany(companyId, companyUpdateDto);
    return ResponseEntity.ok(companyResponseDto);
  }

  @DeleteMapping("/{companyId}")
  public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId, @RequestBody CompanyDeleteDto companyDeleteDto) {
    companyService.deleteCompany(companyId, companyDeleteDto);
    return ResponseEntity.ok().build();
  }
}