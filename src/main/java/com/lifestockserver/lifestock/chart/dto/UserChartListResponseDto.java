package com.lifestockserver.lifestock.chart.dto;

import java.util.List;

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
public class UserChartListResponseDto extends ChartListResponseDto {
  private Long userId;
  private List<UserChartElementResponseDto> chartList;
}
