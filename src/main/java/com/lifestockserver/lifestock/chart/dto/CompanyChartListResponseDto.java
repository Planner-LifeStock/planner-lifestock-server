package com.lifestockserver.lifestock.chart.dto;

import java.util.List;


abstract class CompanyChartListResponseDto extends ChartListResponseDto {
  protected Long companyId;
  protected List<CompanyChartElementResponseDto> chartList;
}


