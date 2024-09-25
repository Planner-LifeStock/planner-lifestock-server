package com.lifestockserver.lifestock.todo.dto;

import java.time.DayOfWeek;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoUpdateDto {
  private String description;
  private Set<DayOfWeek> days;
  private boolean completed;
}
