package com.lifestockserver.lifestock.chart.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyChartMonthlyListResponseDto extends CompanyChartListResponseDto {
  private LocalDate month;
}
