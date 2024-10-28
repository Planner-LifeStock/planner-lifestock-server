package com.lifestockserver.lifestock.todo.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

import com.lifestockserver.lifestock.todo.domain.enums.TodoLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TodoResponseDto {
  private Long id;
  private Long companyId;
  private String title;
  private String description;
  private boolean completed;
  private boolean done;
  private TodoLevel level;
  private LocalDate startDate;
  private LocalDate endDate;
  private Set<DayOfWeek> days;
}
