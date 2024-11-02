package com.lifestockserver.lifestock.todo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoUpdateDto {
  private String description;
  private boolean completed;
}
