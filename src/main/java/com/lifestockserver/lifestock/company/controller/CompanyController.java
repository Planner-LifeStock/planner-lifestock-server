package com.lifestockserver.lifestock.company.controller;

import com.lifestockserver.lifestock.company.dto.CompanyCreateDto;
import com.lifestockserver.lifestock.company.dto.CompanyDeleteDto;
import com.lifestockserver.lifestock.company.service.CompanyService;
import com.lifestockserver.lifestock.company.dto.CompanyResponseDto;
import com.lifestockserver.lifestock.company.dto.CompanyUpdateDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.lifestockserver.lifestock.user.domain.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {

  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @GetMapping
  public ResponseEntity<List<CompanyResponseDto>> getCompaniesByUserId(
    @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    List<CompanyResponseDto> companyResponseDtos = companyService.findAllByUserId(userDetails.getUserId());
    return ResponseEntity.ok(companyResponseDtos);
  }
  
  @GetMapping("/{companyId}")
  public ResponseEntity<CompanyResponseDto> getCompany(@PathVariable Long companyId) {
    CompanyResponseDto companyResponseDto = companyService.findById(companyId);
    return ResponseEntity.ok(companyResponseDto);
  }

  @PostMapping
  public ResponseEntity<CompanyResponseDto> createCompany(
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @RequestBody CompanyCreateDto companyCreateDto
  ) {
    CompanyResponseDto companyResponseDto = companyService.createCompany(userDetails.getUserId(), companyCreateDto);
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