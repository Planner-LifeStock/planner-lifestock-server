package com.lifestockserver.lifestock.todo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoCompletedResponseDto {
  private Long id;
  private Long currentPrice;
  private double changeRate;
}
