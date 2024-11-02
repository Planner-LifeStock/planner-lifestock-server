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
  public TodoResponseDto createTodo(Long userId, TodoCreateDto todoCreateDto) {
    if (todoCreateDto.getStartDate().isAfter(todoCreateDto.getEndDate())) {
      throw new RuntimeException("시작일이 종료일보다 클 수 없습니다");
    }
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
    Company company = companyRepository.findById(todoCreateDto.getCompanyId())
      .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다: " + todoCreateDto.getCompanyId()));
    if (user.getId() != company.getUser().getId()) {
      throw new RuntimeException("사용자와 회사의 관계가 올바르지 않습니다: " + user.getId());
    }

    Todo savedTodo = null;

    if (todoCreateDto.getDays() == null) {
      Todo todo = todoMapper.toEntity(todoCreateDto, user, company);
      savedTodo = todoRepository.save(todo);
      return todoMapper.toDto(savedTodo);
    }

    // startDate부터 endDate까지 반복하여 todo 생성
    LocalDate date = todoCreateDto.getStartDate();
    LocalDate endDate = todoCreateDto.getEndDate().plusDays(1);
    while (date.isBefore(endDate)) {
      if (!todoCreateDto.getDays().contains(date.getDayOfWeek())) {
        date = date.plusDays(1);
        continue;
      }

      TodoCreateDto dailyTodoCreateDto = TodoCreateDto.builder()
        .companyId(todoCreateDto.getCompanyId())
        .title(todoCreateDto.getTitle())
        .description(todoCreateDto.getDescription())
        .level(todoCreateDto.getLevel())
        .startDate(date)
        .endDate(date)
        .build();

      Todo todo = todoMapper.toEntity(dailyTodoCreateDto, user, company);
      savedTodo = todoRepository.save(todo);
      date = date.plusDays(1);
    }

    if (savedTodo == null) {
      throw new RuntimeException("해당 기간 내에 생성할 수 있는 todo가 없습니다.");
    }
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
    if (todo.getStartDate().isAfter(LocalDate.now())) {
      throw new RuntimeException("아직 시작하지 않은 todo입니다");
    }

    todo.setCompleted(true);
    Todo updatedTodo = todoRepository.save(todo);

    // 추후 chart 업데이트되는 로직 추가
    ChartCreateDto chartCreateDto = ChartCreateDto.builder()
      .companyId(todo.getCompany().getId())
      .todoId(todo.getId())
      .build();
    
    ChartResponseDto chartResponseDto = chartService.createChart(todo.getUser().getId(), chartCreateDto);

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

  @Transactional
  public void deleteTodosAfterDateByCompanyId(Long companyId, LocalDate date) {
    List<Todo> todos = todoRepository.findAllByCompanyIdAndDateAfter(companyId, date);

    for (Todo todo : todos) {
      todo.setDeletedAt(LocalDateTime.now());
    }
    todoRepository.saveAll(todos);
  }
}
