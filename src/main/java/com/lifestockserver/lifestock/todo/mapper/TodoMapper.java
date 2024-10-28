package com.lifestockserver.lifestock.todo.mapper;

import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.dto.TodoCreateDto;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {
  public Todo toEntity(TodoCreateDto dto, User user, Company company) {
    return Todo.builder()
      .user(user)
      .company(company)
      .title(dto.getTitle())
      .description(dto.getDescription())
      .level(dto.getLevel())
      .startDate(dto.getStartDate())
      .endDate(dto.getEndDate())
      .days(dto.getDays())
      .build();
  }

  public TodoResponseDto toDto(Todo todo) {
    return TodoResponseDto.builder()
      .id(todo.getId())
      .companyId(todo.getCompany().getId())
      .title(todo.getTitle())
      .description(todo.getDescription())
      .completed(todo.isCompleted())
      .done(todo.isDone())
      .level(todo.getLevel())
      .startDate(todo.getStartDate())
      .endDate(todo.getEndDate())
      .days(todo.getDays())
      .build();
  }
}
