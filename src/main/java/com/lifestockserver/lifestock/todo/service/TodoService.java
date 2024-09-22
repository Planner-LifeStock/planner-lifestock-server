package com.lifestockserver.lifestock.todo.service;

import org.springframework.stereotype.Service;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.dto.TodoCreateDto;
import com.lifestockserver.lifestock.todo.mapper.TodoMapper;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class TodoService {
  private final TodoRepository todoRepository;
  private final TodoMapper todoMapper;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;

  public TodoService(TodoRepository todoRepository, TodoMapper todoMapper, UserRepository userRepository, CompanyRepository companyRepository) {
    this.todoRepository = todoRepository;
    this.todoMapper = todoMapper;
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
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

  public TodoResponseDto createTodo(TodoCreateDto todoCreateDto) {
    if (todoCreateDto.getStartDate().isAfter(todoCreateDto.getEndDate())) {
      throw new RuntimeException("시작일이 종료일보다 클 수 없습니다");
    }

    User user = userRepository.findById(todoCreateDto.getUserId())
      .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    Company company = companyRepository.findById(todoCreateDto.getCompanyId())
      .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다"));
    
    if (user.getId() != company.getUser().getId()) {
      throw new RuntimeException("사용자와 회사의 관계가 올바르지 않습니다");
    }

    Todo todo = todoMapper.toEntity(todoCreateDto, user, company);
    Todo savedTodo = todoRepository.save(todo);

    // 추후 chart 업데이트되는 로직 추가
    return todoMapper.toDto(savedTodo);
  }
}