package com.lifestockserver.lifestock.todo.service;

import org.springframework.stereotype.Service;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;
import com.lifestockserver.lifestock.todo.domain.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
  private final TodoRepository todoRepository;

  public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  public List<TodoResponseDto> getAllTodosByDate(Long userId, Long companyId, LocalDate date) {
    List<Todo> todos = todoRepository.findAllByUserIdAndCompanyIdAndDate(userId, companyId, date);
    
    List<TodoResponseDto> todoResponseDtos = todos.stream()
      .map(todo -> TodoResponseDto.builder()
        .id(todo.getId())
        .userId(todo.getUser().getId())
        .companyId(todo.getCompany().getId())
        .title(todo.getTitle())
        .description(todo.getDescription())
        .completed(todo.isCompleted())
        .done(todo.isDone())
        .build())
      .collect(Collectors.toList());
    return todoResponseDtos;
  }
}