package com.lifestockserver.lifestock.company.controller;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.company.service.CompanyService;
import com.lifestockserver.lifestock.user.domain.User;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class CompanyController {

    private final CompanyService companyService;


    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/savedCompany")
    public ResponseEntity<?> createCompany(@RequestBody Company company){
        try {
            Company savedCompany = companyService.saveCompany(company);
            return ResponseEntity.ok(savedCompany);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Invalid input : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred : "  + e.getMessage());
        }
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id){
        try{
            Optional<Company> company = companyService.getCompanyById(id);

            return company.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id){
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
