package com.lifestockserver.lifestock.chart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCurrentPriceListResponseDto {
  private Long userId;
  private List<CompanyCurrentPriceResponseDto> companyCurrentPriceList;
  private Long totalChangeRate;
  private Long totalCurrentPrice;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CompanyCurrentPriceResponseDto {
  private Long companyId;
  private Long currentPrice;
  private Long changeRate;
}
