package com.lifestockserver.lifestock.chart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartResponseDto {
  private Long open;
  private Long high;
  private Long low;
  private Long close;
  private LocalDateTime date;
  private double changeRate;
}