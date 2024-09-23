package com.lifestockserver.lifestock.chart.dto;

import java.util.List;

// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @AllArgsConstructor
// @Getter
// @Setter
// @NoArgsConstructor
abstract class ChartListResponseDto {
  protected Long userId;
  protected List<?> chartList;
  protected int totalPages;
  protected int currentPage;
  protected int limit;
}
