package com.lifestockserver.lifestock.chart.dto;

import java.time.LocalDate;

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
public class ChartCreateDto {
  private Long companyId;
  private Long userId;
  private Long todoId;

  private Long open;
  private Long high;
  private Long low;
  private Long close;
  private LocalDate date;
  private boolean isAfterMarketOpen;
}
