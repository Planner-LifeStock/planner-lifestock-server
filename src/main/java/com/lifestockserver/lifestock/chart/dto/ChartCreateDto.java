package com.lifestockserver.lifestock.chart.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChartCreateDto {
  private Long companyId;
  private Long userId;
  private Long todoId;
  private Long close;
}
