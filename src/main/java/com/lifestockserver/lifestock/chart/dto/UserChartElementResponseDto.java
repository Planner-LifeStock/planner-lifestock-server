package com.lifestockserver.lifestock.chart.dto;

import java.time.LocalDateTime;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserChartElementResponseDto {
  private Long id;
  private Long companyId;
  private Long open;
  private Long high;
  private Long low;
  private Long close;
  private LocalDateTime createdAt;
}
