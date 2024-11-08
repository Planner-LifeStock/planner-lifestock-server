package com.lifestockserver.lifestock.company.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;

import com.lifestockserver.lifestock.file.dto.FileResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CompanyResponseDto {
  private Long id;
  private String name;
  private String description;
  private CompanyLevel level;
  private CompanyLeastOperatePeriod leastOperatePeriod;
  private LocalDate listedDate;
  private Long investmentAmount;
  private Long initialStockPrice;
  private Long initialStockQuantity;
  private FileResponseDto logo;
  private Long openStockPrice;
  private Long currentStockPrice;
  private Long listedStockPrice;
  private LocalDateTime createdAt;
}
