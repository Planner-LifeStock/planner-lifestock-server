package com.lifestockserver.lifestock.company.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class CompanyController {

  @PostMapping
  public ResponseEntity<Company> createCompany(@RequestBody CompanyCreateDto companyCreateDto) {
    Company company = companyService.createCompany(companyCreateDto);
    return ResponseEntity.ok(company);
  }
}