package com.lifestockserver.lifestock.chart.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyChartPageResponseDto extends CompanyChartListResponseDto {
  private int pageNumber;
  private int pageSize;
  private int totalPages;
  private long totalElements;
  private boolean first;
  private boolean last;
  private List<CompanyChartElementResponseDto> chartList;
}