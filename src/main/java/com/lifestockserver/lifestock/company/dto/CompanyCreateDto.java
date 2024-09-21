package com.lifestockserver.lifestock.company.dto;

import lombok.*;

import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCreateDto {
  private User user;
  private String name;
  private String description;
  private CompanyLevel level;
  private CompanyLeastOperatePeriod leastOperatePeriod;
  private LocalDate listedDate;
  private Long investmentAmount;
  private Long initialStockPrice;
}