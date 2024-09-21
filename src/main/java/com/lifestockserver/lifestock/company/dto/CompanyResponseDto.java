package com.lifestockserver.lifestock.company.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;

import com.lifestockserver.lifestock.file.domain.File;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CompanyResponseDto {
  private String name;
  private String description;
  private CompanyLevel level;
  private CompanyLeastOperatePeriod leastOperatePeriod;
  private LocalDate listedDate;
  private Long investmentAmount;
  private Long initialStockPrice;
  private Long initialStockQuantity;
  private File logo;
  private Long currentStockPrice;
}
