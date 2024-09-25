package com.lifestockserver.lifestock.chart.dto;

import java.util.List;


abstract class ChartListResponseDto {
  protected Long userId;
  protected List<?> chartList;
}
