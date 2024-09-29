package com.lifestockserver.lifestock.chart.dto;

import java.time.LocalDateTime;

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
  private LocalDateTime date;
  @Builder.Default
  private boolean isAfterMarketOpen = true;
}
