package com.lifestockserver.lifestock.todo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lifestockserver.lifestock.todo.repository.TodoRepository;
import com.lifestockserver.lifestock.todo.dto.TodoResponseDto;
import com.lifestockserver.lifestock.todo.domain.Todo;
import com.lifestockserver.lifestock.todo.dto.TodoCreateDto;
import com.lifestockserver.lifestock.todo.dto.TodoCompletedResponseDto;
import com.lifestockserver.lifestock.chart.dto.ChartCreateDto;
import com.lifestockserver.lifestock.chart.dto.ChartResponseDto;
import com.lifestockserver.lifestock.todo.mapper.TodoMapper;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import com.lifestockserver.lifestock.company.repository.CompanyRepository;
import com.lifestockserver.lifestock.chart.service.ChartService;
import com.lifestockserver.lifestock.company.domain.Company;
import com.lifestockserver.lifestock.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class TodoService {
  private final TodoRepository todoRepository;
  private final TodoMapper todoMapper;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final ChartService chartService;

  public TodoService(TodoRepository todoRepository, TodoMapper todoMapper, UserRepository userRepository, CompanyRepository companyRepository, ChartService chartService) {
    this.todoRepository = todoRepository;
    this.todoMapper = todoMapper;
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.chartService = chartService;
  }

  public List<TodoResponseDto> getAllTodosByDate(Long userId, Long companyId, LocalDate date) {
    List<Todo> todos = todoRepository.findAllByUserIdAndCompanyIdAndDate(userId, companyId, date);
    
    List<TodoResponseDto> todoResponseDtos = todos.stream()
        .filter(todo -> todo.getDays().contains(date.getDayOfWeek()))
        .map(todoMapper::toDto)
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

  @Transactional
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
    
    return todoMapper.toDto(savedTodo);
  }

  @Transactional
  public TodoCompletedResponseDto updateTodoCompleted(Long id) {
    Todo todo = todoRepository.findByIdAndDeletedAtIsNull(id);
    if (todo == null) {
      throw new RuntimeException("해당 id의 todo를 찾을 수 없습니다");
    }
    if (todo.isDone()) {
      throw new RuntimeException("기한이 만료된 todo입니다");
    }

    todo.setCompleted(true);
    Todo updatedTodo = todoRepository.save(todo);

    // 추후 chart 업데이트되는 로직 추가
    ChartCreateDto chartCreateDto = ChartCreateDto.builder()
      .companyId(todo.getCompany().getId())
      .userId(todo.getUser().getId())
      .todoId(todo.getId())
      .build();
    ChartResponseDto chartResponseDto = chartService.createChart(chartCreateDto);

    return TodoCompletedResponseDto.builder()
      .id(updatedTodo.getId())
      .currentPrice(chartResponseDto.getClose())
      .changeRate((double) (chartResponseDto.getClose() - chartResponseDto.getOpen()) / chartResponseDto.getOpen() * 100)
      .build();
  }

  public void deleteTodo(Long id, String deletedReason) {
    Todo todo = todoRepository.findByIdAndDeletedAtIsNull(id);
    if (todo == null) {
      throw new RuntimeException("해당 id의 todo를 찾을 수 없습니다");
    }

    todo.setDeletedAt(LocalDateTime.now());
    todo.setDeletedReason(deletedReason);
    todoRepository.save(todo);
  }
}