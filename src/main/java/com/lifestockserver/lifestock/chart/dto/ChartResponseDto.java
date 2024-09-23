package com.lifestockserver.lifestock.chart.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChartResponseDto {
  private Long id;
  private Long todoId;
  private Long open;
  private Long high;
  private Long low;
  private Long close;

  private LocalDateTime createdAt;
}
