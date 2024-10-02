package com.lifestockserver.lifestock.chart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCurrentPriceResponseDto {
  private Long companyId;
  private Long currentPrice;
  private double changeRate;
}
