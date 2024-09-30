package com.lifestockserver.lifestock.company.dto;

import lombok.*;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLeastOperatePeriod;
import com.lifestockserver.lifestock.company.domain.enums.CompanyLevel;
import com.lifestockserver.lifestock.user.domain.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCreateDto {
  private Long userId;
  private User user;
  private String name;
  private String description;
  private CompanyLevel level;
  private CompanyLeastOperatePeriod leastOperatePeriod;
  private Long investmentAmount;
  private Long initialStockQuantity;
  private Long initialStockPrice;
  private String logoFileId;
}