package com.lifestockserver.lifestock.todo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.lifestockserver.lifestock.todo.domain.enums.TodoLevel;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Set;

@Getter
@Setter
@Builder
public class TodoCreateDto {
  private Long userId;
  private Long companyId;
  private String title;
  private String description;
  private TodoLevel level;
  private LocalDate startDate;
  private LocalDate endDate;
  private Set<DayOfWeek> days;
}