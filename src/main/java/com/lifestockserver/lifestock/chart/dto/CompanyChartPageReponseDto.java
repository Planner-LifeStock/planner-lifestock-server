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
public class CompanyChartPageReponseDto {
  private Long companyId;
  private List<ChartResponseDto> chartList;
  private int totalPages;
  private long totalElements;
  private int pageSize;
  private int pageNumber;
  private boolean hasNext;
  private boolean hasPrevious;
}
