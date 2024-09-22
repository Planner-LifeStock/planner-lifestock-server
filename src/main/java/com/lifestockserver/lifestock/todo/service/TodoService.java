package com.lifestockserver.lifestock.todo.service;

import org.springframework.stereotype.Service;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.mapper.TodoMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class TodoService {
  private final TodoRepository todoRepository;
  private final TodoMapper todoMapper;

  public TodoService(TodoRepository todoRepository, TodoMapper todoMapper) {
    this.todoRepository = todoRepository;
    this.todoMapper = todoMapper;
  }

  public List<TodoResponseDto> getAllTodosByDate(Long userId, Long companyId, LocalDate date) {
    List<Todo> todos = todoRepository.findAllByUserIdAndCompanyIdAndDate(userId, companyId, date);
    
    List<TodoResponseDto> todoResponseDtos = todos.stream()
      .map(todo -> todoMapper.toDto(todo))
      .collect(Collectors.toList());
        
    return todoResponseDtos;
  }

  public List<TodoResponseDto> getMonthlyTodos(Long userId, Long companyId, LocalDate date) {
    List<Todo> todos = todoRepository.findAllByUserIdAndCompanyIdAndMonth(userId, companyId, date);
    
    List<TodoResponseDto> todoResponseDtos = todos.stream()
      .map(todo -> todoMapper.toDto(todo))
      .collect(Collectors.toList());
      
    // startDate으로 정렬
    todoResponseDtos.sort(Comparator.comparing(TodoResponseDto::getStartDate));
    return todoResponseDtos;
  }
}