package com.lifestockserver.lifestock.company.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.chart.repository.ChartRepository;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;

@Service
public class CompanyService {

  private final ChartRepository chartRepository;
  private final CompanyRepository companyRepository;
  
  public CompanyService(ChartRepository chartRepository, CompanyRepository companyRepository) {
    this.chartRepository = chartRepository;
    this.companyRepository = companyRepository;
  }

  public Company getCompanyWithStockPrice(Long companyId) {

    Company company = companyRepository.findById(companyId)
      .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    // 가장 최근의 high 값을 가져와서 currentStockPrice에 설정
    chartRepository.findLatestHighByCompanyId(companyId).ifPresent(latestHigh -> company.setCurrentStockPrice(latestHigh));

    return company;
  }
}
