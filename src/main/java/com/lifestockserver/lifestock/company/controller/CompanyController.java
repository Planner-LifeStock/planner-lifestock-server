package com.lifestockserver.lifestock.company.controller;

import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.service.CompanyService;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {

  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @PostMapping
  public ResponseEntity<CompanyResponseDto> createCompany(@RequestBody CompanyCreateDto companyCreateDto, String userId) {
    CompanyResponseDto companyResponseDto = companyService.createCompany(companyCreateDto);
    return ResponseEntity.ok(companyResponseDto);
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

}