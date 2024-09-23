package com.lifestockserver.lifestock.chart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyChartListResponseDto extends ChartListResponseDto {
  private Long companyId;
  private List<CompanyChartElementResponseDto> chartList;
}
