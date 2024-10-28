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
  private List<CompanyCurrentPriceResponseDto> companyCurrentPriceList;
  private double totalChangeRate;
  private Long totalCurrentPrice;
}
