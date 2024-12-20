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
  private Long todoId;

  private Long open;
  private Long high;
  private Long low;
  private Long close;
  @Builder.Default
  private LocalDate date = LocalDate.now();
  @Builder.Default
  private boolean isAfterMarketOpen = true;
}
